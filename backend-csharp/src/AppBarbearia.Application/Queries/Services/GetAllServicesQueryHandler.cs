using MediatR;
using AppBarbearia.Application.Common;
using AppBarbearia.Domain.Interfaces;

namespace AppBarbearia.Application.Queries.Services;

public sealed class GetAllServicesQueryHandler(IServiceRepository repository)
    : IRequestHandler<GetAllServicesQuery, Result<IEnumerable<ServiceDto>>>
{
    public async Task<Result<IEnumerable<ServiceDto>>> Handle(GetAllServicesQuery request, CancellationToken cancellationToken)
    {
        var services = request.OnlyActive
            ? await repository.GetActiveServicesAsync(cancellationToken)
            : await repository.GetAllAsync(cancellationToken);

        var dtos = services.Select(s => new ServiceDto(
            s.Id, s.Name, s.Description, s.Price, s.DurationMinutes, s.IsActive, s.CreatedAt, s.UpdatedAt));

        return Result<IEnumerable<ServiceDto>>.Success(dtos);
    }
}
