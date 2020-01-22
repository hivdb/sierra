# Major refactoring of Sierra

## Done
- Decouple HIV related data from Sierra code and save them in hivfacts
- Create a new library "sierra-core" to contain all core code from
  Sierra

## TODO
- Fix all test cases
- Create a new library "sierra-web-hiv" to contain all website code
  from Sierra (migrate from WebApplications)
- Migrate and rewrite in Python: the remaining data generation code
  to `hivfacts/scripts`
