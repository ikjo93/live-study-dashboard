import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

public class DashBoard {

    private static final String REPOSITORY_NAME = "ikjo93/live-study-dashboard";
    // private static final String REPOSITORY_NAME = "whiteship/live-study";

    public void run() throws IOException {
        GitHub gitHub = new GitHubBuilder()
            .withOAuthToken(System.getenv("PERSONAL_ACCESS_TOKEN"))
            .build();

        GHRepository repository = gitHub.getRepository(REPOSITORY_NAME);

        List<GHIssue> issues = repository.getIssues(GHIssueState.ALL);

        Map<String, Integer> attendanceByUser = new HashMap<>();

        for (GHIssue issue : issues) {
            List<GHIssueComment> comments = issue.getComments();

            Set<String> userBuffer = new HashSet<>();

            for (GHIssueComment comment : comments) {
                userBuffer.add(comment.getUser().getLogin());
            }

            for (String user : userBuffer) {
                attendanceByUser.put(user, attendanceByUser.getOrDefault(user, 0) + 1);
            }
        }

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        for (String user : attendanceByUser.keySet()) {
            double rate = (double) (attendanceByUser.get(user) * 100) / issues.size();
            bw.write(String.format("%s님의 출석율은 %.2f%%입니다.%n", user, rate));
        }

        bw.flush();
        bw.close();
    }
}
