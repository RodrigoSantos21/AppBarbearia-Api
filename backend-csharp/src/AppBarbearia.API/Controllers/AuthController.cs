using MediatR;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using AppBarbearia.Application.Commands.Auth;
using AppBarbearia.Application.Queries.Auth;
using System.Security.Claims;

namespace AppBarbearia.API.Controllers;

[ApiController]
[Route("api/[controller]")]
[Produces("application/json")]
public class AuthController(IMediator mediator) : ControllerBase
{
    [HttpPost("register")]
    [AllowAnonymous]
    public async Task<IActionResult> Register([FromBody] RegisterClientCommand command, CancellationToken ct = default)
    {
        var result = await mediator.Send(command, ct);
        return result.IsSuccess
            ? CreatedAtAction(nameof(Me), new { }, new { id = result.Value })
            : BadRequest(result.Error);
    }

    [HttpPost("login")]
    [AllowAnonymous]
    public async Task<IActionResult> Login([FromBody] LoginCommand command, CancellationToken ct = default)
    {
        var result = await mediator.Send(command, ct);
        return result.IsSuccess ? Ok(result.Value) : Unauthorized(result.Error);
    }

    [HttpGet("me")]
    [Authorize]
    public async Task<IActionResult> Me(CancellationToken ct = default)
    {
        var userId = GetCurrentUserId();
        if (userId is null) return Unauthorized();

        var result = await mediator.Send(new GetUserByIdQuery(userId.Value), ct);
        return result.IsSuccess ? Ok(result.Value) : NotFound(result.Error);
    }

    [HttpPut("profile")]
    [Authorize]
    public async Task<IActionResult> UpdateProfile([FromBody] UpdateProfileRequest request, CancellationToken ct = default)
    {
        var userId = GetCurrentUserId();
        if (userId is null) return Unauthorized();

        var command = new UpdateProfileCommand(userId.Value, request.FullName, request.Phone);
        var result = await mediator.Send(command, ct);
        return result.IsSuccess ? NoContent() : BadRequest(result.Error);
    }

    [HttpPut("password")]
    [Authorize]
    public async Task<IActionResult> ChangePassword([FromBody] ChangePasswordRequest request, CancellationToken ct = default)
    {
        var userId = GetCurrentUserId();
        if (userId is null) return Unauthorized();

        var command = new ChangePasswordCommand(userId.Value, request.CurrentPassword, request.NewPassword, request.ConfirmNewPassword);
        var result = await mediator.Send(command, ct);
        return result.IsSuccess ? NoContent() : BadRequest(result.Error);
    }

    [HttpGet("barbers")]
    [Authorize]
    public async Task<IActionResult> GetBarbers(CancellationToken ct = default)
    {
        var result = await mediator.Send(new GetAllBarbersQuery(), ct);
        return result.IsSuccess ? Ok(result.Value) : BadRequest(result.Error);
    }

    [HttpPost("barbers")]
    [Authorize(Roles = "Admin")]
    public async Task<IActionResult> RegisterBarber([FromBody] RegisterBarberCommand command, CancellationToken ct = default)
    {
        var result = await mediator.Send(command, ct);
        return result.IsSuccess
            ? CreatedAtAction(nameof(GetBarbers), new { }, new { id = result.Value })
            : BadRequest(result.Error);
    }


    [HttpGet("users")]
    [Authorize(Roles = "Admin")]
    public async Task<IActionResult> GetAllUsers([FromQuery] string? role, CancellationToken ct = default)
    {
        var result = await mediator.Send(new GetAllUsersQuery(role), ct);
        return result.IsSuccess ? Ok(result.Value) : BadRequest(result.Error);
    }

    [HttpPut("users/{id:guid}/status")]
    [Authorize(Roles = "Admin")]
    public async Task<IActionResult> SetUserActiveStatus(Guid id, [FromBody] SetUserActiveStatusRequest request, CancellationToken ct = default)
    {
        var command = new SetUserActiveStatusCommand(id, request.IsActive);
        var result = await mediator.Send(command, ct);
        return result.IsSuccess ? NoContent() : BadRequest(result.Error);
    }

    private Guid? GetCurrentUserId()
    {
        var sub = User.FindFirstValue(System.IdentityModel.Tokens.Jwt.JwtRegisteredClaimNames.Sub)
               ?? User.FindFirstValue(ClaimTypes.NameIdentifier);
        return Guid.TryParse(sub, out var id) ? id : null;
    }
}


public record UpdateProfileRequest(string FullName, string Phone);
public record ChangePasswordRequest(string CurrentPassword, string NewPassword, string ConfirmNewPassword);

public record SetUserActiveStatusRequest(bool IsActive);