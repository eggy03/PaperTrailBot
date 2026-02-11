package io.github.eggy03.papertrail.bot.commons.constant;

import lombok.NonNull;

public class ProjectInfo {
	
	private ProjectInfo() {
		throw new IllegalStateException("Utility Class");
	}

	@NonNull
	public static final String APPNAME = "PaperTrail";

	@NonNull
	public static final String VERSION = "v2.5.0";

	@NonNull
	public static final String PROJECT_ISSUE_LINK="https://github.com/eggy03/PaperTrailBot/issues";
}
