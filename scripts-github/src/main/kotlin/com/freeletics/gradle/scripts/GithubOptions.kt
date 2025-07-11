package com.freeletics.gradle.scripts

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

public class GithubOptions : OptionGroup("Github options") {
    public val userName: String by option(
        "--project-username",
        envvar = "GITHUB_REPOSITORY_OWNER",
        help = "The username that this project belongs to",
    ).required()

    public val repoSlug: String by option(
        "--project-repository",
        envvar = "GITHUB_REPOSITORY",
        help = "The repository for this project",
    ).required()

    public val repoName: String
        get() = repoSlug.split("/")[1]

    private val serverUrl: String by option(
        "--server-url",
        envvar = "GITHUB_SERVER_URL",
        help = "The repository for this project",
    ).required()

    public val runId: String by option(
        "--run-id",
        envvar = "GITHUB_RUN_ID",
        help = "The repository for this project",
    ).required()

    public val jobUrl: String
        get() = "$serverUrl/$repoSlug/actions/runs/$runId"

    public val jobName: String by option(
        "--job-name",
        envvar = "GITHUB_JOB",
        help = "The name of the job",
    ).required()

    public val buildNumber: String by option(
        "--build-number",
        envvar = "GITHUB_RUN_ID",
        help = "The number of the job",
    ).required()

    public val branch: String by option(
        "--branch",
        envvar = "GITHUB_REF_NAME",
        help = "The branch that was built",
    ).required()

    public val commitSha1: String by option(
        "--commit-sha1",
        envvar = "GITHUB_SHA",
        help = "The sha1 of the commit that was built",
    ).required()
}
