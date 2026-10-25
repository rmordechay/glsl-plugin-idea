# Releasing

The plugin is published to the [JetBrains Marketplace](https://plugins.jetbrains.com/) on two channels:

| Channel      | Who gets it                                               | Published when                     | Version                         |
|--------------|-----------------------------------------------------------|------------------------------------|---------------------------------|
| `Stable`     | Everyone who installs the plugin from the Marketplace     | A GitHub release is published      | The release tag, e.g. `1.2.0`   |
| `glsl-alpha` | Only users who added the alpha repository (see below)     | Every push to `main` (merged PR)   | e.g. `1.2.0-alpha.7`            |

Both are published by [`.github/workflows/publish.yml`](.github/workflows/publish.yml). Publishing from a local machine
is possible, but only for emergencies (see [Publishing from a local machine](#publishing-from-a-local-machine)).

## Versions

`pluginVersion` in `gradle.properties` is always the version of the **next** stable release - the one `main` is
working towards. Alpha builds are pre-releases of that version: `1.2.0-alpha.7` is the 7th workflow run's build of
what will become `1.2.0`.

An IDE orders these versions like this (checked with the platform's own version comparison):

```
1.1.6  <  1.2.0-alpha.1  <  1.2.0-alpha.7  <  1.2.0-alpha.12  <  1.2.0  <  1.3.0-alpha.13  <  1.3.0
```

So an alpha build comes **before** the release it leads up to: `1.2.0-alpha.12` is older than `1.2.0`. When `1.2.0`
is released, alpha users are updated to it like everyone else. Alpha numbers are compared as numbers, not text
(`alpha.7` < `alpha.12`).

The number after `alpha.` is the workflow's run number. It never resets, so it keeps counting up across releases
(`1.2.0-alpha.12` may be followed by `1.3.0-alpha.13`); it isn't "the 13th alpha of 1.3.0".

This is why `pluginVersion` has to be bumped **right after** each release: if `main` still says `1.2.0` after
`1.2.0` is out, the next alpha builds are `1.2.0-alpha.N`, which are *older* than the installed `1.2.0`, so alpha
users never get them.

## Changelog

The plugin's change notes (shown in the IDE and on the Marketplace) are generated from `CHANGELOG.md`, from the
section whose heading matches the version:

- A stable build `1.2.0` uses the `## [1.2.0]` section.
- An alpha build `1.2.0-alpha.7` uses the section of the version it leads up to, also `## [1.2.0]`.
- If that section doesn't exist, the build fails with `Version is missing: 1.2.0`. So every version needs its
  section **before** anything is built for it - including alpha builds.

### Example: from one release to the next

Say `1.2.0` was just released. The release PR bumps `pluginVersion` to `1.3.0` and adds an empty section for it:

```markdown
# GLSL Plugin Changelog

## [1.3.0]

## [1.2.0]

### Fixed
- Fixed the IDE eventually becoming unable to parse any file
...
```

Every PR merged from now on adds its entries to `[1.3.0]`, and each merge publishes an alpha build. After a few
merges, the changelog looks like this:

```markdown
# GLSL Plugin Changelog

## [1.3.0]

### Added
- Added support for ray tracing shader file extensions

### Fixed
- Fixed function overload resolution for `vec3` arguments

## [1.2.0]
...
```

| Merged PR                        | Published build    | Channel      | Change notes shown                               |
|----------------------------------|--------------------|--------------|--------------------------------------------------|
| The release PR (bump to `1.3.0`) | `1.3.0-alpha.13`   | `glsl-alpha` | `[1.3.0]` - still empty                          |
| Ray tracing extensions           | `1.3.0-alpha.14`   | `glsl-alpha` | `[1.3.0]` - the "Added" entry                    |
| Overload resolution fix          | `1.3.0-alpha.15`   | `glsl-alpha` | `[1.3.0]` - both entries                         |
| *GitHub release `1.3.0`*         | `1.3.0`            | `Stable`     | `[1.3.0]` - both entries                         |

Alpha users get `1.3.0-alpha.13`, `.14` and `.15` as updates, and then `1.3.0` itself. Stable users go straight
from `1.2.0` to `1.3.0`.

## The pipeline

[`publish.yml`](.github/workflows/publish.yml) has one job per channel. Both run the tests (under Xvfb with the Mesa
software renderer, because `ShaderProgramCompilerTest` needs an OpenGL context) and the plugin verifier, in the same
Gradle invocation as the publish - **nothing is published unless the tests and the verifier pass.**

- **`publish-alpha`** runs on every push to `main`. It reads `pluginVersion` from `gradle.properties`, appends
  `-alpha.<run number>`, and publishes to `glsl-alpha`. Marketplace creates the channel on the first publish.
- **`publish-stable`** runs when a GitHub release is published. It takes the version from the release's tag (a
  leading `v` is stripped, so `1.3.0` and `v1.3.0` both work), publishes to `Stable`, and attaches the plugin zip to
  the GitHub release.

Pushing a tag on its own does **not** publish anything; only publishing a GitHub release does.

The Marketplace token is the `JETBRAINS_TOKEN` repository secret.

## Releasing a stable version

1. **Check the changelog.** The `[x.y.z]` section must describe everything since the last release, and its heading
   must match `pluginVersion` in `gradle.properties`.
2. **Publish a GitHub release** from `main`, with a new tag named after the version (e.g. `1.3.0`). The pipeline
   tests, verifies and publishes it to `Stable` and attaches the zip to the release. Marketplace may take a while to
   approve a new stable version before users see it.
3. **Bump the version right away**, in a PR: set `pluginVersion` to the next version and add an empty section for it
   at the top of `CHANGELOG.md` (see the example above). Until this is merged, alpha users don't get updates.

## Publishing from a local machine

**Don't, unless it's an emergency.** Releases and alpha builds are published by the pipeline. A local publish
skips everything that makes a pipeline build trustworthy: it's built from whatever is on your machine (local changes,
unpushed commits, your own JDK and environment) instead of a reviewed commit on `main`, and nothing records which
code it was built from.

An emergency is something like a critical fix that has to reach users while the pipeline can't publish it, e.g.
because GitHub Actions is down or the workflow itself is broken. If you have to publish locally:

- Build from a clean checkout of the exact commit you're publishing (`main`, or the release tag), with no local
  changes.
- Tell the other maintainers which version you published, and from which commit.
- Fix whatever kept the pipeline from publishing, so the next release goes through it again.

You need a Marketplace token: on the Marketplace, open your profile → *My Tokens* → *Generate Token*. Pass it through
an environment variable rather than on the command line, so it doesn't end up in your shell history:

```shell
export ORG_GRADLE_PROJECT_intellijPlatformPublishingToken='<your token>'
```

(On PowerShell: `$env:ORG_GRADLE_PROJECT_intellijPlatformPublishingToken = '<your token>'`.)

The tests need an OpenGL context. On a machine with a GPU that just works; elsewhere, run them the way the pipeline
does (`xvfb-run` with `LIBGL_ALWAYS_SOFTWARE=1`).

Marketplace rejects a version that has already been published, so every publish needs a version that hasn't been used
before - also not by the pipeline.

### To `glsl-alpha`

Take the latest alpha version the pipeline published (see the plugin's page on the Marketplace, or the latest
*Publish Plugin* run on GitHub) and append another number. If the latest is `1.3.0-alpha.15`:

```shell
./gradlew test verifyPlugin publishPlugin -PpluginVersion=1.3.0-alpha.15.1 -PpluginChannels=glsl-alpha
```

`1.3.0-alpha.15.1` sorts after `1.3.0-alpha.15` and before `1.3.0-alpha.16`, and the pipeline never produces a version
with an extra number, so it can't collide with a later pipeline build. For another local build, use `.15.2`.

Without `-PpluginChannels`, the publish goes to `glsl-alpha` too: the build defaults to it, so a publish can't reach
Stable users by accident.

### To `Stable`

This replaces step 2 of [Releasing a stable version](#releasing-a-stable-version):

```shell
./gradlew test verifyPlugin publishPlugin -PpluginVersion=1.3.0 -PpluginChannels=Stable
git tag 1.3.0
git push origin 1.3.0
```

Then, optionally, create a GitHub release for the existing tag and attach `build/distributions/*.zip` by hand.
Creating the release starts the pipeline, which will try to publish `1.3.0` again; Marketplace rejects the duplicate,
so that run fails - that's expected, and the version you published is unaffected.

Afterwards, bump the version as in step 3 of [Releasing a stable version](#releasing-a-stable-version).

## Installing alpha builds

In the IDE: *Settings → Plugins → ⚙ → Manage Plugin Repositories…*, and add

```
https://plugins.jetbrains.com/plugins/glsl-alpha/list
```

The IDE then offers alpha builds as updates. Remove the repository again to go back to stable releases only (from the
next stable release on).

## Troubleshooting

| Problem                                              | Cause and fix                                                                                                   |
|------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| Build fails with `Version is missing: x.y.z`         | `CHANGELOG.md` has no `## [x.y.z]` section. Add it (empty is fine for alpha builds).                             |
| Publish fails because the version already exists     | That exact version was published before. Pick a new one; see [To `glsl-alpha`](#to-glsl-alpha).                 |
| Alpha users don't get new alpha builds               | `pluginVersion` wasn't bumped after the last release, so the alpha builds are older than the installed release. |
| Publish fails with an authorization error            | The token is missing, expired or lacks permission for this plugin. Check the `JETBRAINS_TOKEN` secret (or your local variable). |
