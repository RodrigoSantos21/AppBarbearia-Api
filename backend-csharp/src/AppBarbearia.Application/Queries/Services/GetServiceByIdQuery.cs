using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Queries.Services;

public record GetServiceByIdQuery(Guid Id) : IRequest<Result<ServiceDto>>;
