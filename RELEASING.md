# Releasing

 1. Update `CHANGELOG.md` file with relevant info and date;
 2. Update version numbers:
  - `README.md`
 3. Execute the release process: `gradlew release`.
 4. Update release information on https://github.com/nhaarman/ellie/releases;
 5. Increment PATCH version for next snapshot release in `gradle.properties`;
 6. `git commit -am "Prepare next SNAPSHOT release"`, `git push origin dev`;
 7. Grab a coffee.