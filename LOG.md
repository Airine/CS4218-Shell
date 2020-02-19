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
---

Notice: Please kindly do logs while finding and fixing bugs and follow the format. 
Which I think would be very helpful for writing or presenting 
project progress.

## 2020-2-19

1. update EchoApplicationTest for 100% coverage