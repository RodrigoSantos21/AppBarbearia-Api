using MediatR;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using AppBarbearia.Application.Commands.Appointments;
using AppBarbearia.Application.Queries.Appointments;
using System.Security.Claims;

namespace AppBarbearia.API.Controllers;

[ApiController]
[Route("api/[controller]")]
[Produces("application/json")]
[Authorize]
public class AppointmentsController(IMediator mediator) : ControllerBase
{
    [HttpPost]
    public async Task<IActionResult> Create([FromBody] CreateAppointmentRequest request, CancellationToken ct = default)
    {
        var userId = GetCurrentUserId();
        if (userId is null) return Unauthorized();

        var command = new CreateAppointmentCommand(userId.Value, request.BarberId, request.ServiceId, request.ScheduledAt);
        var result  = await mediator.Send(command, ct);

        return result.IsSuccess
            ? CreatedAtAction(nameof(GetById), new { id = result.Value }, new { id = result.Value })
            : BadRequest(result.Error);
    }

    [HttpGet("{id:guid}")]
    public async Task<IActionResult> GetById(Guid id, CancellationToken ct = default)
    {
        var result = await mediator.Send(new GetAppointmentByIdQuery(id), ct);
        return result.IsSuccess ? Ok(result.Value) : NotFound(result.Error);
    }

    [HttpGet("my")]
    public async Task<IActionResult> GetMine(CancellationToken ct = default)
    {
        var userId = GetCurrentUserId();
        if (userId is null) return Unauthorized();

        var result = await mediator.Send(new GetMyAppointmentsQuery(userId.Value), ct);
        return result.IsSuccess ? Ok(result.Value) : BadRequest(result.Error);
    }

    [HttpPost("{id:guid}/cancel")]
    public async Task<IActionResult> Cancel(Guid id, [FromBody] CancelRequest request, CancellationToken ct = default)
    {
        var userId = GetCurrentUserId();
        if (userId is null) return Unauthorized();

        var result = await mediator.Send(new CancelAppointmentCommand(id, userId.Value, request.Reason), ct);
        return result.IsSuccess ? NoContent() : BadRequest(result.Error);
    }

    [HttpPost("{id:guid}/reschedule")]
    public async Task<IActionResult> Reschedule(Guid id, [FromBody] RescheduleRequest request, CancellationToken ct = default)
    {
        var userId = GetCurrentUserId();
        if (userId is null) return Unauthorized();

        var result = await mediator.Send(new RescheduleAppointmentCommand(id, userId.Value, request.NewScheduledAt), ct);
        return result.IsSuccess ? NoContent() : BadRequest(result.Error);
    }

    [HttpGet("agenda")]
    [Authorize(Roles = "Barber,Admin")]
    public async Task<IActionResult> GetAgenda([FromQuery] DateTime? date, CancellationToken ct = default)
    {
        var userId = GetCurrentUserId();
        if (userId is null) return Unauthorized();

        var targetDate = date?.Date ?? DateTime.UtcNow.Date;
        var result     = await mediator.Send(new GetBarberAgendaQuery(userId.Value, targetDate), ct);
        return result.IsSuccess ? Ok(result.Value) : BadRequest(result.Error);
    }

    [HttpGet("agenda/{barberId:guid}")]
    [Authorize(Roles = "Admin")]
    public async Task<IActionResult> GetBarberAgenda(Guid barberId, [FromQuery] DateTime? date, CancellationToken ct = default)
    {
        var targetDate = date?.Date ?? DateTime.UtcNow.Date;
        var result     = await mediator.Send(new GetBarberAgendaQuery(barberId, targetDate), ct);
        return result.IsSuccess ? Ok(result.Value) : BadRequest(result.Error);
    }

    [HttpPost("{id:guid}/confirm")]
    [Authorize(Roles = "Barber,Admin")]
    public async Task<IActionResult> Confirm(Guid id, CancellationToken ct = default)
    {
        var userId = GetCurrentUserId();
        if (userId is null) return Unauthorized();

        var result = await mediator.Send(new ConfirmAppointmentCommand(id, userId.Value), ct);
        return result.IsSuccess ? NoContent() : BadRequest(result.Error);
    }

    [HttpPost("{id:guid}/finish")]
    [Authorize(Roles = "Barber,Admin")]
    public async Task<IActionResult> Finish(Guid id, CancellationToken ct = default)
    {
        var userId = GetCurrentUserId();
        if (userId is null) return Unauthorized();

        var result = await mediator.Send(new FinishAppointmentCommand(id, userId.Value), ct);
        return result.IsSuccess ? NoContent() : BadRequest(result.Error);
    }


    private Guid? GetCurrentUserId()
    {
        var sub = User.FindFirstValue(System.IdentityModel.Tokens.Jwt.JwtRegisteredClaimNames.Sub)
               ?? User.FindFirstValue(ClaimTypes.NameIdentifier);
        return Guid.TryParse(sub, out var id) ? id : null;
    }
}

public record CreateAppointmentRequest(Guid BarberId, Guid ServiceId, DateTime ScheduledAt);
public record CancelRequest(string Reason);
public record RescheduleRequest(DateTime NewScheduledAt);
