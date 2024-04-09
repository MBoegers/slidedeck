* clone https://github.com/moderneinc/rewrite-recipe-starter
* mvn test > in der Zeit die Pipelines zeigen
* remove Recipes and Tests
* Update pom.xml
  * io.github.mboegers.rewrite.ws:starter-workshop
  * dependencies hinzufügen
    * org.testng:testng:compile
    * org.junit.jupiter:junit-jupiter-api:compile
  * mvn validate
* Erzeuge Test zum Migrieren der Lifecycle
  * Implements RewriteTest markiert ihn als Test für OpenRewrite
    * defaults konfiguriert den Context
    * spec.parser(JavaParser.fromJavaVersion()
       .logCompilationWarningsAndErrors(true)
       .classpath("junit-jupiter-api", "testng"))
       .recipe(new AddTestLifecyleToJUnitTests());
    * define is and should
      * ```java
        import org.junit.jupiter.api.BeforeAll;
        class MyTest {
        @BeforeAll
        static void setup() {/* do my before setup */}
        }
        ```
      * ```java
        class MyTest {
        @BeforeClass
        static void setup() {/* do my before setup */}
        }
        ```
      * put together test as 
      ```java
      rewriteRun(java(is, should));
      ```
  * implementiere Recipe, bzw. konfiguriere
    * we change the Type of the Annotation, lets check Recipe Katalog
      * ```yaml
        ---
        type: specs.openrewrite.org/v1beta/recipe
        name: io.github.mboegers.rewrite.ws.starter.MigrateSetup
        displayName: Change TestNG Setup Annotations to JUnit Jupiter
        recipeList:
          - org.openrewrite.java.ChangeType:
            oldFullyQualifiedTypeName: org.testng.annotations.BeforeClass
            newFullyQualifiedTypeName: org.junit.jupiter.api.BeforeAll
            ignoreDefinition: false
        ```
       * Run test again and see failure due to missing newline at imports, thats because of the style
* Migriere Assertions
  * Setup Test with missing Recipe
  * define is as
  ```java
    import org.testng.Assert;
          
    class MyTest {
      void test() {
        int expected = 3, actual = 5;
        Assert.assertEquals(actual, expected, "Test fails badly");
      }
    }
  ```
  * define is should 
  ```java
    import org.testng.Assert;
          
    class MyTest {
      void test() {
        int expected = 3, actual = 5;
        Assertions.assertEquals(expected, actual, "Test failed badly");
      }
    }
  ```
  * see the switch in order of parameters!
  * this is detectable with patterns, to be precise Refaster Templates
  * Neue Klasse mit RecipeDescriptor versehen
    * name ist hier der Display name, nicht der Idenficator
    * descriptor ist markdown als erklärung
    * die Annotation hat zwei Ziele, Doku und dem Compiler sagen, mache hier heraus rezepte
  * inner static class mit zwei Methoden welche die Before und After sind
    * ```java
       @BeforeTemplate
        void testng(Object actual, Object expected, String msg) {
            Assert.assertEquals(actual, expected, msg);
        }
        @AfterTemplate
        void jupiter(Object actual, Object expected, String msg) {
            Assertions.assertEquals(expected, actual, msg);
        }
      ```
    * test laufen lasse, Achtung Layout (tabs) müssen gleich sein!!
* Migrate Test Annotation
  * Plan
    * only if empty or no parameters
    * otherwise skip
    * Replace with other @Test
  * Add Test with 
    ```java
    @Language("JAVA") String is = """
          import org.testng.annotations.Test;
          
          class MyTest {
              @Test
              void test() {}
          }
          """;
        @Language("JAVA") String should = """
          import org.junit.jupiter.api.Test;
          
          class MyTest {
              @Test
              void test() {}
          }
          """;
    ```
    * and with @Test()
    * and with @Test(enabled = false)
  * create  Migration Recipe via context action 
    * add Display name and Description accordingly, Description needs a .!!
    * overwright getVisitor and create visitor via context action
    * switch to JavaIsoVisitor because we are handling Standard java code
    * Talk shortly about Vistors and show `System.out.println(TreeVisitingPrinter.printTree(getCursor()));` or https://docs.openrewrite.org/concepts-explanations/tree-visiting-printer
    * show visitors, we need a Method invocation
    * in Visitor
      * call super
      * getLeadingAnnotaions
      * Check if TestNG @Test is present mit einem AnnotationMatcher und Signature @org.testng.annotations.Test
      * We have to write Java, we do this via a JavaTemplate
      ```java
      method = JavaTemplate
        .builder("@Test")
        .javaParser(JavaParser.fromJavaVersion().classpath("junit-jupiter-api"))
        .imports("org.junit.jupiter.api.Test")
        .build()
        .apply(getCursor(), addAnnotationCoordinate);
      ```
      * we need a cursor to tell the template where we are in the tree via getCursor
      * and we need to tell OR what to do, add an Annotation `method.getCoordinates().addAnnotation(Comparator.comparing(J.Annotation::getSimpleName))`
      * Test, Oh es passiert etwas aber nicht alles richtig.. okay anscheinend ist die Alte Annotation noch da also laufen wir mehrfach los..
        * wir lösen das mit dem RemoveAnnotationVisitor `method = new RemoveAnnotationVisitor(TESTNG_TEST_MATCHER).visitMethodDeclaration(method, executionContext);`
      * Testen, oh anscheinend wurden die Imports nicht angepast, da gibt es Methoden am Visitor Maybe add/remove
      ```java
      maybeAddImport("org.junit.jupiter.api.Test"); maybeRemoveImport("org.testng.annotations.Test");
      ```
      * lass uns da mal mit dem Debugger rein schauen, ah die Arguments sind nicht leer also noch abbrechen mit
      ```java
       List<Expression> arguments = testNgAnnotation.get().getArguments();
       if (arguments != null && !arguments.isEmpty()) {
           return method;
       }
      ```
      * warum klappt es immer noch nicht, Debugger! naja weil es gibt Argumente in der Annotation, eben Empty
        * wir müssen schauen ob keiner der parameter vom Type J-Empty ist `arguments.stream().noneMatch(J.Empty.class::isInstance)`
* Was haben wir den nun geschafft
  * Wir überführen Setup Methoden
  * Wir migrieren Assertions
  * Wir migrieren leere @Test Annotations
  * Es fehlt nun die migrations von Argumenten zu anderen Annotations
* Grundsätzlich habe ihr nun alle wichtigen Werkzeuge an der Hand
  * Tests schreiben was man haben will
  * Debuggen und den LST Printen lassen um zu sehen wo man ist
  * Verwendung von Vorhandenen Recipes, Templates und Wiederverwendung von anderen Visitoren
*  Wie kann ich jetzt weiter machen?
  * Helft mir dabei OpenJDK zu helfen mit https://github.com/MBoegers/migrate-testngtojupiter-rewrite
  * Helft OpenRewrite oder unterhaltet euch im Slack
  * Schau in euren Projekten, wo haben wir wiederkehrende Refactorings und kommt auf uns als Contributoren zu. Es ist und bleibt ein junges Open Source Tool. Es kann nicht alles aber kann alles lernen! schreibet ein Request, bekommt hilfe beim Umsetzten und habt Spaß daran euren Code mit Code besser zu machen.

