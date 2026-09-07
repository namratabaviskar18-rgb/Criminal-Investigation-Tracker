using System.Data;
using System.Data.SqlClient;
using ComplaintMonitoringSystem.Models;

namespace ComplaintMonitoringSystem.Data
{
    public class DatabaseHelper
    {
        // Change this connection string according to your SQL Server
        private static string connStr = 
            "Data Source=.;Initial Catalog=ComplaintDB;Integrated Security=True;TrustServerCertificate=True";

        // -------------------- REGISTER --------------------
        public static bool Register(RegisterRequest req)
        {
            using (SqlConnection con = new SqlConnection(connStr))
            {
                string query = @"INSERT INTO Users (FullName, Username, Password, Email, Phone, Role)
                                 VALUES (@n, @u, @p, @e, @ph, 'User')";
                SqlCommand cmd = new SqlCommand(query, con);
                cmd.Parameters.AddWithValue("@n", req.FullName);
                cmd.Parameters.AddWithValue("@u", req.Username);
                cmd.Parameters.AddWithValue("@p", req.Password);
                cmd.Parameters.AddWithValue("@e", (object)req.Email ?? DBNull.Value);
                cmd.Parameters.AddWithValue("@ph", (object)req.Phone ?? DBNull.Value);
                con.Open();
                try
                {
                    return cmd.ExecuteNonQuery() > 0;
                }
                catch
                {
                    return false; // Username already exists
                }
            }
        }

        // -------------------- LOGIN --------------------
        public static User Login(string username, string password)
        {
            using (SqlConnection con = new SqlConnection(connStr))
            {
                string query = "SELECT * FROM Users WHERE Username=@u AND Password=@p";
                SqlCommand cmd = new SqlCommand(query, con);
                cmd.Parameters.AddWithValue("@u", username);
                cmd.Parameters.AddWithValue("@p", password);
                con.Open();
                SqlDataReader dr = cmd.ExecuteReader();
                if (dr.Read())
                {
                    return new User
                    {
                        UserID = Convert.ToInt32(dr["UserID"]),
                        FullName = dr["FullName"].ToString(),
                        Username = dr["Username"].ToString(),
                        Email = dr["Email"]?.ToString(),
                        Phone = dr["Phone"]?.ToString(),
                        Role = dr["Role"].ToString()
                    };
                }
            }
            return null;
        }

        // -------------------- POST COMPLAINT --------------------
        public static bool PostComplaint(PostComplaintRequest req)
        {
            using (SqlConnection con = new SqlConnection(connStr))
            {
                string query = @"INSERT INTO Complaints (UserID, Subject, Description)
                                 VALUES (@uid, @sub, @desc)";
                SqlCommand cmd = new SqlCommand(query, con);
                cmd.Parameters.AddWithValue("@uid", req.UserID);
                cmd.Parameters.AddWithValue("@sub", req.Subject);
                cmd.Parameters.AddWithValue("@desc", req.Description);
                con.Open();
                return cmd.ExecuteNonQuery() > 0;
            }
        }

        // -------------------- GET COMPLAINTS BY USER --------------------
        public static List<Complaint> GetComplaintsByUser(int userId)
        {
            List<Complaint> list = new List<Complaint>();
            using (SqlConnection con = new SqlConnection(connStr))
            {
                string query = @"SELECT c.*, u.FullName, u.Username 
                                 FROM Complaints c 
                                 INNER JOIN Users u ON c.UserID = u.UserID
                                 WHERE c.UserID = @uid
                                 ORDER BY c.CreatedAt DESC";
                SqlCommand cmd = new SqlCommand(query, con);
                cmd.Parameters.AddWithValue("@uid", userId);
                con.Open();
                SqlDataReader dr = cmd.ExecuteReader();
                while (dr.Read())
                {
                    list.Add(MapComplaint(dr));
                }
            }
            return list;
        }

        // -------------------- GET ALL COMPLAINTS (ADMIN) --------------------
        public static List<Complaint> GetAllComplaints()
        {
            List<Complaint> list = new List<Complaint>();
            using (SqlConnection con = new SqlConnection(connStr))
            {
                string query = @"SELECT c.*, u.FullName, u.Username 
                                 FROM Complaints c 
                                 INNER JOIN Users u ON c.UserID = u.UserID
                                 ORDER BY c.CreatedAt DESC";
                SqlCommand cmd = new SqlCommand(query, con);
                con.Open();
                SqlDataReader dr = cmd.ExecuteReader();
                while (dr.Read())
                {
                    list.Add(MapComplaint(dr));
                }
            }
            return list;
        }

        // -------------------- REPLY TO COMPLAINT --------------------
        public static bool ReplyToComplaint(ReplyRequest req)
        {
            using (SqlConnection con = new SqlConnection(connStr))
            {
                string query = @"UPDATE Complaints 
                                 SET AdminReply = @reply, 
                                     Status = @status, 
                                     RepliedAt = GETDATE()
                                 WHERE ComplaintID = @id";
                SqlCommand cmd = new SqlCommand(query, con);
                cmd.Parameters.AddWithValue("@reply", req.AdminReply);
                cmd.Parameters.AddWithValue("@status", req.Status);
                cmd.Parameters.AddWithValue("@id", req.ComplaintID);
                con.Open();
                return cmd.ExecuteNonQuery() > 0;
            }
        }

        private static Complaint MapComplaint(SqlDataReader dr)
        {
            return new Complaint
            {
                ComplaintID = Convert.ToInt32(dr["ComplaintID"]),
                UserID = Convert.ToInt32(dr["UserID"]),
                Subject = dr["Subject"].ToString(),
                Description = dr["Description"].ToString(),
                Status = dr["Status"].ToString(),
                AdminReply = dr["AdminReply"] == DBNull.Value ? null : dr["AdminReply"].ToString(),
                CreatedAt = Convert.ToDateTime(dr["CreatedAt"]),
                RepliedAt = dr["RepliedAt"] == DBNull.Value ? null : Convert.ToDateTime(dr["RepliedAt"]),
                FullName = dr["FullName"].ToString(),
                Username = dr["Username"].ToString()
            };
        }
    }
}
