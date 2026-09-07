using Microsoft.AspNetCore.Mvc;
using ComplaintMonitoringSystem.Data;
using ComplaintMonitoringSystem.Models;

namespace ComplaintMonitoringSystem.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AuthController : ControllerBase
    {
        [HttpPost("register")]
        public IActionResult Register([FromBody] RegisterRequest request)
        {
            if (string.IsNullOrWhiteSpace(request.FullName) ||
                string.IsNullOrWhiteSpace(request.Username) ||
                string.IsNullOrWhiteSpace(request.Password))
            {
                return BadRequest(new { success = false, message = "FullName, Username and Password are required." });
            }

            bool result = DatabaseHelper.Register(request);
            if (result)
                return Ok(new { success = true, message = "Registration successful. You can now login." });
            else
                return BadRequest(new { success = false, message = "Username already exists." });
        }

        [HttpPost("login")]
        public IActionResult Login([FromBody] LoginRequest request)
        {
            if (string.IsNullOrWhiteSpace(request.Username) || string.IsNullOrWhiteSpace(request.Password))
                return BadRequest(new { success = false, message = "Username and Password required." });

            var user = DatabaseHelper.Login(request.Username, request.Password);
            if (user == null)
                return Unauthorized(new { success = false, message = "Invalid username or password." });

            return Ok(new
            {
                success = true,
                message = "Login successful",
                user = new
                {
                    user.UserID,
                    user.FullName,
                    user.Username,
                    user.Email,
                    user.Phone,
                    user.Role
                }
            });
        }
    }
}
