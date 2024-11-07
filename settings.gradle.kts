pluginManagement {
   includeBuild("build-logic")
   repositories {
//      google {
//         content {
//            includeGroupByRegex("com\\.android.*")
//            includeGroupByRegex("com\\.google.*")
//            includeGroupByRegex("androidx.*")
//         }
//      }
      google()
      mavenCentral()
      gradlePluginPortal()
   }
}
dependencyResolutionManagement {
   repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
   repositories {
      google()
      mavenCentral()
   }
}

rootProject.name = "Habit"
include(":app")
include(":core:ui")
include(":core:common")
include(":feature:subject")
include(":core:data")
include(":feature:homework")
include(":feature:overview")
include(":feature:exam")
include(":feature:reminder")
include(":datetime")
include(":feature:calendar")
include(":feature:onboarding")
include(":feature:thesisplanner")
include(":feature:lecture")
include(":feature:auth")
include(":feature:settings")
