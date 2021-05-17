Hi! Thanks for taking the time to look at this. There were a few things I was a little unsure of, but left it a little late.

# Some notes / assumptions
* The file will not contain any of the possible delimiters " ", ",", "|" as part of the data we care about
* I assumed the data we got would always be good, and didn't go so far as to make it handle bad data, return 400s, or errors - no validation
* It currently only supports the date format of "MM/dd/yyyy" when receiving
* Using datomic mem, I know I didn't have to. Only in the API though. It wouldnt be much to add it to the CLI but I ran out of time
* I used component, because I like it, however, have never done it in this way with the command line portion, but I wanted it to have a similar interface to the api, and will simplify if we do add dependencies
* I didn't get to do all the testing I would like. Ran out of time
* I like to use httpie, so the example requests are using httpie
* I should have used `birthdate`, but I used `birthday`
* Didn't do the cli test runner, ran out of time.

# How to run me from the repl
1. Start a repl
2. Load the user namespace
3. `(go)`

# How to run from commandline

You'll need to create the files before running this. Sorry.

`clj -X:cli '[:files :comma-file-path]' '"/home/peter/Downloads/testfilecomma"' '[:files :pipe-file-path]' '"/home/peter/Downloads/testfilepipe"' '[:files :space-file-path]' '"/home/peter/Downloads/testfilespace"** :output 3`

# Useful commands

## Load data through the api
http -v POST localhost:4500/records  data='Me | Peter | peter@me.me | red | 10/20/2020'

## Create API post requests

```
http -v POST localhost:4500/records  data='Gabby | Ashley | gabby@ashley.me | green | 09/09/1957'
http -v POST localhost:4500/records  data='Peter | Steele | peter@steele.me | blue | 03/02/1999'
http -v POST localhost:4500/records  data='Icecream | Sandwich | peter@steele.me | yellow | 04/23/1979'
```

## Get API requests

```
http -v GET localhost:4500/records/color
http -v GET localhost:4500/records/birthdate
http -v GET localhost:4500/records/name
```

## CLI

You'll need to create the files before running this. Sorry.

`clj -X:cli '[:files :comma-file-path]' '"/home/peter/Downloads/testfilecomma"' '[:files :pipe-file-path]' '"/home/peter/Downloads/testfilepipe"' '[:files :space-file-path]' '"/home/peter/Downloads/testfilespace"' :output 1`




## Example of the files

```
Bigly | Strong | bigstrong@iam.me | blue | 05/24/1956
Mediumly | Really | reallymediumly@iam.me | blue | 05/24/1956
Small | NotStrong | small@notstrong.me | orange | 11/12/1962

```
