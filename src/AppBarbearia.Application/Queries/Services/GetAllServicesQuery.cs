using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Queries.Services;

public record GetAllServicesQuery(bool OnlyActive = true) : IRequest<Result<IEnumerable<ServiceDto>>>;
