(ns monkey.oci.fn.test.core-test
  (:require [clojure.test :refer [deftest testing is with-test]]
            [monkey.oci.fn.core :as sut]
            [martian.test :as mt])
  (:import java.security.KeyPairGenerator))

(defn generate-key []
  (-> (doto (KeyPairGenerator/getInstance "RSA")
        (.initialize 2048))
      (.generateKeyPair)
      (.getPrivate)))

(def test-config {:user-ocid "test-user"
                  :tenancy-ocid "test-tenancy"
                  :private-key (generate-key)
                  :key-fingerprint "test-fingerprint"
                  :region "test-region"})

(def test-ctx (sut/make-context test-config))

(defn- test-call
  ([f route opts]
   (let [r (-> test-ctx
               (mt/respond-with-constant {route {:body "[]" :status 200}})
               (f opts)
               (deref))]
     (is (map? r))
     (is (= 200 (:status r)))))
  ([route opts]
   (let [f (some->> route symbol (ns-resolve 'monkey.oci.fn.core) var-get)]
     (is (fn? f) (str "no binding found for " route))
     (when f
       (test-call f route opts)))))

;; Since all tests are more or less the same, let's use a seq instead of copy/pasting.

(defn test-endpoints [ep]
  (doseq [[k v] ep]
    (testing (format "invokes `%s` endpoint" k)
      (test-call k v))))

(deftest application-endpoints
  (test-endpoints {:list-applications  {:compartment-id "test-compartment"}
                   :create-application {:compartment-id "new-compartment"
                                        :subnet-ids ["test-subnet"]
                                        :display-name "test-app"}
                   :update-application {:application-id "test-app"
                                        :config {"key" "value"}}
                   :delete-application {:application-id "test-app"}
                   :get-application    {:application-id "test-app"}}))

(deftest function-endpoints
  (test-endpoints {:get-function    {:function-id "test-fn"}
                   :list-functions  {:application-id "test-app"}
                   :create-function {:application-id "test-app"
                                     :display-name "test-fn"
                                     :image "test:latest"
                                     :memory-in-m-bs 100}
                   :update-function {:function-id "test-fn"
                                     :display-name "test-fn"
                                     :image "test:latest"
                                     :memory-in-m-bs 100}
                   :delete-function {:function-id "test-fn"}
                   :invoke-function {:function-id "test-fn"
                                     :fn-invoke-type "wrong"
                                     :body "test body"}}))
