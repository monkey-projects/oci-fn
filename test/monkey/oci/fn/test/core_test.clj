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
   (is (map? (-> test-ctx
                 (mt/respond-with-constant {route {:body "[]"}})
                 (f opts)
                 (deref)))))
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
  (test-endpoints {:list-applications {:compartment-id "test-compartment"}
                   :create-application {:compartment-id "new-compartment"
                                        :subnet-ids ["test-subnet"]
                                        :display-name "test-app"}
                   :update-application {:application-id "test-app"
                                        :config {"key" "value"}}
                   :delete-application {:application-id "test-app"}
                   :get-application {:application-id "test-app"}}))

(deftest function-endpoints
  (test-endpoints {:get-function {:function-id "test-fn"}
                   :list-functions {:application-id "test-app"}}))
