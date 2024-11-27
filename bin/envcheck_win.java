import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.file.LinkOption;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;


import static java.nio.file.Files.getOwner;

class EnvCheck {

    public static void main(String[] args)  throws IOException, InterruptedException {
        try {
            int returnCode = 0;

            // Check Maven
            returnCode += checkTool("cmd", "/c", "C:\\maven\\bin\\mvn.cmd", "-version", "Maven");

            // Check Docker Compose
            returnCode += checkTool("docker", "compose", "version", "Docker Compose");

            // Check Required Directories
            returnCode += checkDirectories(List.of(
                    "./keycloak/extensions/target/classes",
                    "./keycloak/imex",
                    "./keycloak/themes/apps",
                    "./deployments/local/dev/run/keycloak/data",
                    "./keycloak/extensions/target/classes",
                    "./keycloak/themes/internal",
                    "./keycloak/config",
                    "./keycloak/cli"
            ));

            // Exit with the accumulated return code
            System.exit(returnCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Check if a tool is installed and available.
     */
    private static int checkTool(String... commandAndArgs) throws IOException, InterruptedException {
        String toolName = commandAndArgs[commandAndArgs.length - 1];
        System.out.printf("Checking %s...%n", toolName);

        try {
            var pb = new ProcessBuilder(commandAndArgs);
            pb.inheritIO();
            var process = pb.start();
            int returnCode = process.waitFor();

            if (returnCode > 0) {
                System.out.printf("Please install %s.%n", toolName);
            }
            return returnCode;

        } catch (IOException | InterruptedException e) {
            System.err.printf("Error while checking %s: %s%n", toolName, e.getMessage());
            return 1;
        }
    }

    /**
     * Check if the required directories exist and have the correct owner.
     */
    private static int checkDirectories(List<String> directories) {
        int returnCode = 0;

        for (String dirPath : directories) {
            var dir = new File(dirPath);

            if (!dir.exists()) {
                System.out.printf("Path \"%s\" does not exist. Please create it or build the project with Maven.%n", dirPath);
                returnCode++;
                continue;
            }

            try {
                String currentUser = System.getProperty("user.name");
                UserPrincipal owner = getOwner(dir.toPath(), LinkOption.NOFOLLOW_LINKS);

                if (!currentUser.equals(owner.getName())) {
                    System.out.printf("Path \"%s\" has the wrong owner \"%s\". Please adjust it to \"%s\".%n", dirPath, owner.getName(), currentUser);
                    returnCode++;
                }
            } catch (IOException e) {
                System.err.printf("Error checking owner of path \"%s\": %s%n", dirPath, e.getMessage());
                returnCode++;
            }
        }

        return returnCode;
    }
}
