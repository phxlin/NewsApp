package lin.yufan.newsapp.data.remote

const val inValidGetBreakingNewsResponse =
    "{\n" +
            "  \"status\": \"error\",\n" +
            "  \"code\": \"parametersMissing\",\n" +
            "  \"message\": \"Required parameters are missing. Please set any of the following parameters and try again: sources, q, language, country, category.\"\n" +
            "}"