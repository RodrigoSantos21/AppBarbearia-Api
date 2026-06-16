using MediatR;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using AppBarbearia.Application.Commands.Services;
using AppBarbearia.Application.Queries.Services;

namespace AppBarbearia.API.Controllers;

[ApiController]
[Route("api/[controller]")]
[Produces("application/json")]
[Authorize]
public class ServicesController(IMediator mediator) : ControllerBase
{
    [HttpGet]
    public async Task<IActionResult> GetAll([FromQuery] bool onlyActive = true, CancellationToken ct = default)
    {
        var result = await mediator.Send(new GetAllServicesQuery(onlyActive), ct);
        return result.IsSuccess ? Ok(result.Value) : BadRequest(result.Error);
    }

    [HttpGet("{id:guid}")]
    public async Task<IActionResult> GetById(Guid id, CancellationToken ct = default)
    {
        var result = await mediator.Send(new GetServiceByIdQuery(id), ct);
        return result.IsSuccess ? Ok(result.Value) : NotFound(result.Error);
    }

    [HttpPost]
    [Authorize(Roles = "Barber,Admin")]
    public async Task<IActionResult> Create([FromBody] CreateServiceCommand command, CancellationToken ct = default)
    {
        var result = await mediator.Send(command, ct);
        return result.IsSuccess
            ? CreatedAtAction(nameof(GetById), new { id = result.Value }, new { id = result.Value })
            : BadRequest(result.Error);
    }

    [HttpPut("{id:guid}")]
    [Authorize(Roles = "Barber,Admin")]
    public async Task<IActionResult> Update(Guid id, [FromBody] UpdateServiceCommand command, CancellationToken ct = default)
    {
        if (id != command.Id) return BadRequest("Route ID and body ID do not match.");
        var result = await mediator.Send(command, ct);
        return result.IsSuccess ? NoContent() : BadRequest(result.Error);
    }

    [HttpDelete("{id:guid}")]
    [Authorize(Roles = "Admin")]
    public async Task<IActionResult> Delete(Guid id, CancellationToken ct = default)
    {
        var result = await mediator.Send(new DeleteServiceCommand(id), ct);
        return result.IsSuccess ? NoContent() : NotFound(result.Error);
    }
}
