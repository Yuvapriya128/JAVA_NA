*DTO
create request, update, response dto for each entity
there add validations

#use streams to convert while needed or call maptoresponse and maptoentity functions::create them.


* pagination

each page will have same number of values

use this in findall
PageRequest.of(0,2)
pagenumber, pagesize

SQL::: orderby, offset(where starting point) , fetch(how many rows)

* Sort.by(Sort.Direction.ASC,"name")
