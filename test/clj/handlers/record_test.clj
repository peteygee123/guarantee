(ns guarantee.handlers.record-test
  (:require [guarantee.handlers.record :as sut]
            [guarantee.util :as util]
            [datomic.api :as d]
            [clojure.test :refer :all]))

(deftest shape-response
  (testing "returns non-namespaced keyword maps"
    (with-redefs [sut/add-derived-fields (fn [x] x)]
      (is (= (sut/shape-response {:person/first-name "Peter" :person/last-name "Me"})
             {:first-name "Peter" :last-name "Me"}))))
  (testing "adds derived field (:name)"
    (is (= (sut/shape-response {:person/first-name "Peter" :person/last-name "Me"})
           {:first-name "Peter" :last-name "Me" :name "Peter Me"}))))

(deftest creates-records-test
  (testing "Returns a 400 when given malformed data")
  ;; Not the greatest tests I've ever written
  (testing "returns a 201 when given 'good?' data"
    (with-redefs [d/transact (constantly (atom {}))
                  d/entity (fn [db db-id] (util/build-record "Me, Peter, my@email.com, red, 10/18/1985"))]
      (is (= 201
             (:status (sut/create-record {:body-params {:data "Me, Peter, my@email.com, red, 10/18/1985"}})))))))

(deftest get-records-test
  (testing "returns a 200"))
