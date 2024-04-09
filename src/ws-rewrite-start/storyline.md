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
  * inner static class mit zwei methoden welche die Before und After sind
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
