(ns guarantee.util-test
  (:require [guarantee.util :as sut]
            [datomic.api :as d]
            [clojure.test.check.clojure-test :refer (defspec)]
            [clojure.string :as str]
            [guarantee.time :as time]
            [clojure.test.check.properties :as prop]
            [clojure.spec.alpha :as s]
            [clojure.test :refer :all]))

(deftest map-keys-test
  (testing "applies a function to all the keys in a map and returns a map"
    (let [result (sut/map-keys #(Integer/parseInt %) {"1" "some key" "2" "in a map"})]
      (is (map? result))
      (is (every? int? (keys result))))))

(deftest map-vals-test
  (testing "applies a function to all the values in a map and returns a map"
    (let [result (sut/map-vals #(Integer/parseInt %) {:a "1" :b "2"})]
      (is (map? result))
      (is (every? int? (vals result))))))

(deftest parse-data-test
  (testing "Splits the data-string and places it into a vector"
    (is (= ["Me" "Peter" "peter@me.me" "red" "10/16/1985"]
           (sut/parse-data "," "Me,Peter,peter@me.me,red,10/16/1985")))))

(deftest detect-delimiter-test
  (testing "returns nil when no delimiter is detected"
    (is (nil? (sut/detect-delimiter "abc-def-ghi-jkl-mno"))))
  (testing "returns the delimiter when detected"
    (is (= (sut/detect-delimiter "abc|def|ghi|jkl|mno") "\\|" ))))

(deftest sanitize-test
  (testing "removes spaces from the beginning or the end of data-maps values"
    (with-redefs [time/string-to-instant-formatter (fn [formatter birthday zone] birthday)
                  d/squuid (constantly "a uuid")]
      (is (= (sut/sanitize {:person/first-name " Peter " :person/birthday " 10/16/1985"})
             {:person/id "a uuid" :person/first-name "Peter" :person/birthday "10/16/1985"}))))
  (testing "adds an id to the map"
    (with-redefs [time/string-to-instant-formatter (fn [formatter birthday zone] birthday)
                  d/squuid (constantly "a uuid")]
      (is (= (:person/id (sut/sanitize {:person/first-name "Peter" :person/birthday "10/16/1985"}))
             "a uuid"))))
  (testing "creates an instant out of the date-string"
    (let [call-count-atom (atom 0)]
      (with-redefs [time/string-to-instant-formatter (fn [formatter birthday zone]
                                                       (swap! call-count-atom inc))]
        (sut/sanitize {:person/first-name "Peter" :person/birthday " 10/16/1985"})
        (is (= @call-count-atom 1))))))

(deftest build-record-test
  (testing "returns a 'record' map when given a vector of person data"
    (is (= (sut/build-record ["Me" "Peter" "my@email.com" "green" "10/10/2020"])
           #:person{:last-name "Me" :first-name "Peter" :email "my@email.com"
                    :favorite-color "green" :birthday "10/10/2020"}))))
