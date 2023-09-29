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

(deftest create-application
  (testing "invokes `create-application` endpoint"
    (is (map? (-> test-ctx
                  (mt/respond-with-constant {:create-application {:body "{}"}})
                  (sut/create-application {:compartment-id "new-compartment"
                                           :subnet-ids ["test-subnet"]
                                           :display-name "test-app"})
                  (deref))))))

(deftest update-application
  (testing "invokes `update-application` endpoint"
    (is (map? (-> test-ctx
                  (mt/respond-with-constant {:update-application {:body "{}"}})
                  (sut/update-application {:application-id "test-app"
                                           :config {"key" "value"}})
                  (deref))))))

(deftest delete-application
  (testing "invokes `delete-application` endpoint"
    (is (map? (-> test-ctx
                  (mt/respond-with-constant {:delete-application {:body "{}"}})
                  (sut/delete-application {:application-id "test-app"})
                  (deref))))))
