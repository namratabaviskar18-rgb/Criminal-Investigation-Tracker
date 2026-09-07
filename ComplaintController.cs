using Microsoft.AspNetCore.Mvc;
using ComplaintMonitoringSystem.Data;
using ComplaintMonitoringSystem.Models;

namespace ComplaintMonitoringSystem.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ComplaintController : ControllerBase
    {
        // User posts a new complaint
        [HttpPost("post")]
        public IActionResult PostComplaint([FromBody] PostComplaintRequest request)
        {
            if (request.UserID <= 0 || string.IsNullOrWhiteSpace(request.Subject) || string.IsNullOrWhiteSpace(request.Description))
                return BadRequest(new { success = false, message = "Invalid data." });

            bool result = DatabaseHelper.PostComplaint(request);
            if (result)
                return Ok(new { success = true, message = "Complaint submitted successfully." });
            else
                return StatusCode(500, new { success = false, message = "Failed to submit complaint." });
        }

        // User views his own complaints
        [HttpGet("my/{userId}")]
        public IActionResult GetMyComplaints(int userId)
        {
            var list = DatabaseHelper.GetComplaintsByUser(userId);
            return Ok(new { success = true, data = list });
        }

        // Admin views all complaints
        [HttpGet("all")]
        public IActionResult GetAllComplaints()
        {
            var list = DatabaseHelper.GetAllComplaints();
            return Ok(new { success = true, data = list });
        }

        // Admin replies to a complaint
        [HttpPost("reply")]
        public IActionResult Reply([FromBody] ReplyRequest request)
        {
            if (request.ComplaintID <= 0 || string.IsNullOrWhiteSpace(request.AdminReply))
                return BadRequest(new { success = false, message = "Invalid data." });

            bool result = DatabaseHelper.ReplyToComplaint(request);
            if (result)
                return Ok(new { success = true, message = "Reply sent successfully." });
            else
                return StatusCode(500, new { success = false, message = "Failed to send reply." });
        }
    }
}
