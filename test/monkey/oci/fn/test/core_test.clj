(ns monkey.oci.fn.test.core-test
  (:require [clojure.test :refer [deftest testing is]]
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

(deftest list-applications
  (testing "invokes `:list-applications` request"
    (let [ctx (mt/respond-with-constant test-ctx {:list-applications {:body "{}"}})]
      (is (map? @(sut/list-applications ctx {:compartment-id "test-compartment"}))))))
