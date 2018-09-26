# SchoolSplitter

Java utility to handle automatically splitting PDF document with school information into individual documents named based on the school name. This utility is intended to be used to aid in posting new versions of the "Getting In" and "Adults Returning to School" publications.

## How to use

1. Manually remove all other sections of the PDF (previous chapters, non-degree-granting schools, etc)
2. Double click the `SchoolSplitter.jar` file
3. Select the PDF file containing only the school information (see testpdf.pdf for an example)
4. Check the `output` folder for individual documents and confirm file names and contents

## Assumptions

1. There is a header with the school name, address, and/or email address centered at the top of the page
    1. The title of the school is in a larger font than the mailing/email address
    2. The header of the school is NO LARGER than 3.7 inches wide and 1.5 inches tall
    3. The header is positioned 2.3 inches from the left side of the page
    4. The header is positioned 0 inches from the top of the page
2. The school names will not change ("and" to "&", etc) as filenames are based on the school's name

## Development

Build the project and generate runnable jar:
```
mvn install
```
Run the project from the command line:
```
java -jar target/school-splitter-1.0-SNAPSHOT-jar-with-dependencies.jar path/to/input/pdf
```
Or double click the jar file to use the GUI.

### Prerequisites

Uses Maven to handle dependencies and testing.

```
apt-get install maven
```

### Installing

Clone the repository

Install dependencies and generate jar file

```
mvn install
```

Double click `target/school-splitter-1.0-SNAPSHOT-jar-with-dependencies.jar` and select `testpdf.pdf`

Check the `output` folder to see the generated documents.

## Running the tests

`mvn test`

## Deployment

Generate the new jar

```
mvn install
```

Rename `target/school-splitter-1.0-SNAPSHOT-jar-with-dependencies.jar` as desired and copy it into a folder containing an empty `output` directory and this readme

Update the readme as necessary

Copy the folder to the J:/ drive as desired by supervisor of Web team (JPROCTOR at time of writing)

## Author

Brent Klein (BKLEIN), unless something's broken, in which case it was Josh Proctor (JPROCTOR).