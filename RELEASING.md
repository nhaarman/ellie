# Releasing

 1. Update `CHANGELOG.md` file with relevant info and date;
 2. Execute the release process: `gradlew release`.
 3. Update release information on https://github.com/nhaarman/ellie/releases;
 4. Increment PATCH version for next snapshot release in `gradle.properties`;
 5. `git commit -am "Prepare next SNAPSHOT release"`, `git push origin dev`;
 6. Grab a coffee.