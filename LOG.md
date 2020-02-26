## 2020-2-5

1. Find bugs in `impl.util.StringUtils.isBlank()` - Runxin 
    
    - infinite loop (solved)
    
    - 5 test cases: 3 failed, 2 passed (solved)
   
2. Some bugs in `ApplicationRunner` - Runxin

    - switch without breaks (solved)
 
---

## 2020-2-13

#### Solve warnings by pmd

1. Solve loss exception trace lost, use 
`(NewException)new NewException().initCause(e)` to solve it.

2. If the warning is un solvable currently, right click and 
click suppress option

3. Solve wrong null value handler in `IORedirectionHandler`

4. Close BufferedReader stream in `IOUtils`

5. Solve name conflict in `RegexArgument`

6. Solve closeResource warming of pmd in `CallCommand` and `PipeCommand`

7. Extract commonly used string as constant String object in `StringUtilsTest`

---

## 2020-2-16

#### Solved all warnings from pmd

1. Solve issue `close() not recognize by PMD #24`

2. Remove redundant if statement

---

## 2020-2-17

#### Configure pom.xml

- add configuration for setting src and test directory.

- add configuration for running test and package.

If you want to run this app in the terminal
execute this command in the root directory:
>  mvn compile -Pcode-mainclass

1. Solved abnormal exit

## 2020-2-18

#### Complete Implementation and test for Paste

1. Paste can take at most one input stream and arbitrary number of files in our implementation.

2. PMD warnings can be suppressed by adding `\\NOPMD` at the end of the statement.

---

## 2020-2-19

1. Complete unit test cases for `StringUtil`.  - 100% coverage

2. Update EchoApplicationTest - 100% coverage

3. Complete unit test cases for `ArgsParser`. -100% coverage

4. Add more test for rmApp and attend to 100% coverage

---

## 2020-2-26

1. Fix 60% coverage of `ExitApplication`, now 100% coverage.

2. Complete unit test cases for `IOUtils`. - 100% coverage.

3. Handle null `InputStream` in `IOUtils.getLinesFromInputStream(InputStream input)`

4. Refactor and solve [issue](https://github.com/nus-cs4218/cs4218-project-ay1920-s2-2020-team6/issues/8) - No PMD warnings

Notice: Please kindly do logs while finding and fixing bugs and follow the format. 
Which I think would be very helpful for writing or presenting 
project progress.

