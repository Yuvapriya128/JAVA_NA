Validators

| Validator        | Purpose                         | Example                         |
| ---------------- | ------------------------------- | ------------------------------- |
| `@NotBlank`      | Not null, not empty, not spaces | `@NotBlank String name;`        |
| `@NotEmpty`      | Not null and not empty          | `@NotEmpty String tags;`        |
| `@Size(min,max)` | Length range                    | `@Size(min=5,max=20)`           |
| `@Pattern`       | Regex validation                | `@Pattern(regexp="^[A-Z]{3}$")` |
| `@Email`         | Valid email format              | `@Email String email;`          |

| Validator                   | Purpose               | Example                         |
| --------------------------- | --------------------- | ------------------------------- |
| `@Min`                      | Minimum value         | `@Min(18)`                      |
| `@Max`                      | Maximum value         | `@Max(100)`                     |
| `@Positive`                 | > 0                   | `@Positive`                     |
| `@PositiveOrZero`           | ≥ 0                   | `@PositiveOrZero`               |
| `@Negative`                 | < 0                   | `@Negative`                     |
| `@NegativeOrZero`           | ≤ 0                   | `@NegativeOrZero`               |
| `@Digits(integer,fraction)` | Restrict digits       | `@Digits(integer=5,fraction=2)` |
| `@DecimalMin`               | Minimum decimal value | `@DecimalMin("1000.50")`        |
| `@DecimalMax`               | Maximum decimal value | `@DecimalMax("99999.99")`       |

how many digits are allowed before and after the decimal point ::: @Digits

| Validator      | Purpose       |
| -------------- | ------------- |
| `@AssertTrue`  | Must be true  |
| `@AssertFalse` | Must be false |

| Validator        | Purpose                    |
| ---------------- | -------------------------- |
| `@NotEmpty`      | Collection cannot be empty |
| `@Size(min,max)` | Collection size range      |

| Validator  | Purpose          |
| ---------- | ---------------- |
| `@NotNull` | Must not be null |
| `@Null`    | Must be null     |

Hibernate specific validators
| Validator           | Purpose                           |
| ------------------- | --------------------------------- |
| `@Length(min,max)`  | String length                     |
| `@Range(min,max)`   | Numeric range                     |
| `@URL`              | Valid URL                         |
| `@CreditCardNumber` | Credit card format                |
| `@ISBN`             | ISBN validation                   |
| `@UniqueElements`   | No duplicate values in collection |


| Feature                                | `@NotNull`              | `@NotEmpty` | `@NotBlank`       |
| -------------------------------------- | ----------------------- | ----------- | ----------------- |
| Rejects `null`                         | ✅                       | ✅           | ✅                 |
| Rejects `""` (empty string)            | ❌                       | ✅           | ✅                 |
| Rejects `"   "` (only spaces)          | ❌                       | ❌           | ✅                 |
| Works on `String`                      | ✅                       | ✅           | ✅                 |
| Works on `List`, `Set`, `Map`, Array   | ❌                       | ✅           | ❌                 |
| Works on Numbers (`Integer`, `Double`) | ✅                       | ❌           | ❌                 |
| Works on Dates (`LocalDate`, `Date`)   | ✅                       | ❌           | ❌                 |
| Works on Custom Objects                | ✅                       | ❌           | ❌                 |
| Most Common Use                        | Objects, Numbers, Dates | Collections | User-entered Text |


@NotBlank ⊂ @NotEmpty ⊂ @NotNull
@NotNull = value must exist
@NotEmpty = value must exist and not be empty
@NotBlank = value must exist, not be empty, and not be only spaces


JSON
__________

| Annotation              | Purpose                                   | Example                                        |
| ----------------------- | ----------------------------------------- | ---------------------------------------------- |
| `@JsonProperty`         | Rename JSON field                         | `@JsonProperty("seat_no")`                     |
| `@JsonIgnore`           | Ignore field during JSON conversion       | `@JsonIgnore private String password;`         |
| `@JsonIgnoreProperties` | Ignore multiple fields                    | `@JsonIgnoreProperties({"password","salary"})` |
| `@JsonInclude`          | Exclude null/empty values                 | `@JsonInclude(Include.NON_NULL)`               |
| `@JsonFormat`           | Format Date/Time                          | `@JsonFormat(pattern="dd-MM-yyyy")`            |
| `@JsonAlias`            | Accept alternate names while reading JSON | `@JsonAlias({"mobile","phone"})`               |
| `@JsonGetter`           | Custom getter for JSON output             | `@JsonGetter("full_name")`                     |
| `@JsonSetter`           | Custom setter for JSON input              | `@JsonSetter("full_name")`                     |
| `@JsonAnyGetter`        | Convert Map entries into JSON properties  | `Map<String,Object>`                           |
| `@JsonAnySetter`        | Store unknown JSON fields in Map          | `Map<String,Object>`                           |
| `@JsonValue`            | Serialize object as a single value        | Usually Enums                                  |
| `@JsonCreator`          | Custom object creation from JSON          | Constructor mapping                            |
| `@JsonRootName`         | Define root element name                  | `@JsonRootName("ticket")`                      |
| `@JsonPropertyOrder`    | Order JSON fields                         | `@JsonPropertyOrder({"id","name"})`            |
| `@JsonManagedReference` | Parent side of bidirectional relation     | OneToMany                                      |
| `@JsonBackReference`    | Child side of bidirectional relation      | ManyToOne                                      |
| `@JsonIdentityInfo`     | Prevent circular references using IDs     | Parent-Child entities                          |
| `@JsonIgnoreType`       | Ignore entire class                       | Class level                                    |
| `@JsonSerialize`        | Custom serialization                      | Custom serializer                              |
| `@JsonDeserialize`      | Custom deserialization                    | Custom deserializer                            |
| `@JsonUnwrapped`        | Flatten nested object                     | Remove nesting                                 |
| `@JsonView`             | Control fields for different users        | Admin/User views                               |
| `@JsonAutoDetect`       | Control field visibility                  | Private field serialization                    |
| `@JsonTypeInfo`         | Include type metadata                     | Polymorphism                                   |
| `@JsonSubTypes`         | Define child classes                      | Polymorphism                                   |
| `@JsonTypeName`         | Custom subtype name                       | Polymorphism                                   |
| `@JsonFilter`           | Dynamic filtering                         | Runtime filtering                              |
| `@JsonNaming`           | Apply naming strategy                     | Snake case                                     |
| `@JsonRawValue`         | Output raw JSON                           | Embed JSON string                              |
| `@JsonMerge`            | Merge object updates                      | PATCH APIs                                     |

https://chatgpt.com/s/t_6a3b38031dcc8191ac31334d72874c7c
