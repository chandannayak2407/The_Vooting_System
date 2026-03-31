import java.sql.*;
import java.util.Scanner;

public class Main {

    static void registerCandidate(String name, String post) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "INSERT INTO candidates(name, post) VALUES (?, ?)";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, name);
            ps.setString(2, post);

            ps.executeUpdate();
            System.out.println("Candidate Registered Successfully!");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    static void showCandidatesByPost(String post) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM candidates WHERE post=?";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, post);

            ResultSet rs = ps.executeQuery();

            System.out.println("\nCandidates for " + post + ":");
            while (rs.next()) {
                System.out.println(rs.getInt("cand_id") + ". " + rs.getString("name"));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    static void vote(String voterId, String post, int candId) {
        try {
            Connection con = DBConnection.getConnection();

            // Check duplicate vote
            String checkQuery = "SELECT COUNT(*) FROM votes WHERE voter_id=? AND post=?";
            PreparedStatement check = con.prepareStatement(checkQuery);
            check.setString(1, voterId);
            check.setString(2, post);

            ResultSet rs = check.executeQuery();
            rs.next();

            if (rs.getInt(1) > 0) {
                System.out.println("You have already voted for this post!");
                return;
            }

            // Insert vote
            String insertQuery = "INSERT INTO votes(voter_id, cand_id, post) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(insertQuery);
            ps.setString(1, voterId);
            ps.setInt(2, candId);
            ps.setString(3, post);

            ps.executeUpdate();
            System.out.println("Vote Recorded Successfully!");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    static void showResults() {
        try {
            Connection con = DBConnection.getConnection();

            String query = "SELECT c.name, c.post, COUNT(v.vote_id) AS total_votes " +
                           "FROM candidates c LEFT JOIN votes v ON c.cand_id = v.cand_id " +
                           "GROUP BY c.cand_id ORDER BY total_votes DESC";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            System.out.println("\n--- Results ---");
            while (rs.next()) {
                System.out.println(
                    rs.getString("post") + " | " +
                    rs.getString("name") + " -> " +
                    rs.getInt("total_votes") + " votes"
                );
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Voting System =====");
            System.out.println("1. Register Candidate");
            System.out.println("2. Cast Vote");
            System.out.println("3. View Results");
            System.out.println("4. Exit");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // clear buffer

            switch (choice) {

                case 1:
                    System.out.print("Enter Candidate Name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter Post: ");
                    String post = sc.nextLine();

                    registerCandidate(name, post);
                    break;

                case 2:
                    System.out.print("Enter Voter ID: ");
                    String voterId = sc.nextLine();

                    System.out.print("Enter Post: ");
                    String votePost = sc.nextLine();

                    showCandidatesByPost(votePost);

                    System.out.print("Enter Candidate ID: ");
                    int candId = sc.nextInt();
                    sc.nextLine();

                    vote(voterId, votePost, candId);
                    break;

                case 3:
                    showResults();
                    break;

                case 4:
                    System.out.println("Exiting...");
                    System.exit(0);

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}