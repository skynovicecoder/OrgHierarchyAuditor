# OrgHierarchyAuditor

A Java application to validate a company's organizational structure, including:

- Salary fairness (managers earn 20%-50% more than their subordinates on average)
- Reporting chain length (no employee should have more than 4 layers of management)
- Structure violations based on data in a CSV file

## 🔧 Requirements

- Java 21
- Maven 3+

> ℹ️ Tested on:  
> ✅ JDK 21 (Adoptium/OpenJDK)  
> ✅ Maven 3.9+  
> ❌ Not compatible with Java 24 (see known issues below)

## ⚠️ Known Issue with Java 24

If you try to compile using **JDK 24**, you may encounter the following error:
```text
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.11.0:compile (default-compile)
on project org-hierarchy-auditor: Fatal error compiling: java.lang.ExceptionInInitializerError:
com.sun.tools.javac.code.TypeTag :: UNKNOWN -> [Help 1]
```
```text
💥 Why This Happens
Java 24 introduced internal changes to com.sun.tools.javac.code.TypeTag and other compiler internals.
Libraries like Lombok, Maven Compiler Plugin, or even OpenCSV annotation processors may rely on those internal APIs.
This leads to:
ExceptionInInitializerError on TypeTag.UNKNOWN during annotation processing or compilation.
✅ Solution
Use Java 21 (LTS) instead of Java 24.
This project is designed and tested with Java 21
```

## 📂 CSV Input Format

Each line represents one employee:
Id,firstName,lastName,salary,managerId
123,Joe,Doe,60000,
124,Martin,Chekov,45000,123
125,Bob,Ronstad,47000,123
300,Alice,Hasacat,50000,124
305,Brett,Hardleaf,34000,300
...

markdown
Copy
Edit

- CEO will have `managerId` empty.
- File can contain up to 1000 employees.

## 🚀 How to Run

1. Clone the repository
2. Place your CSV file in a known path (e.g. `employees.csv`)
3. Run the app:

### 🧪 Option 1: Run using Maven
```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.bigcompany.orghierarchyauditor.OrgHierarchyAuditorApplication" -Dexec.args="path_to_the_input_file/employees.csv"
```

### 🧪 Option 2: Run using packaged JAR
```bash
mvn clean package

java -jar target/org-hierarchy-auditor-1.0.0.jar /path_to_the_input_file/file_name.csv
# OR run with default testing data in sample.csv
java -jar target/org-hierarchy-auditor-1.0.0.jar src/test/resources/sample.csv
# OR run with default data in employees.csv available in the main resources path
java -jar target/org-hierarchy-auditor-1.0.0.jar
```