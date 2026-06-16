using MediatR;
using AppBarbearia.Application.Common;
using AppBarbearia.Domain.Interfaces;

namespace AppBarbearia.Application.Queries.Services;

public sealed class GetServiceByIdQueryHandler(IServiceRepository repository)
    : IRequestHandler<GetServiceByIdQuery, Result<ServiceDto>>
{
    public async Task<Result<ServiceDto>> Handle(GetServiceByIdQuery request, CancellationToken cancellationToken)
    {
        var service = await repository.GetByIdAsync(request.Id, cancellationToken);
        if (service is null)
            return Result<ServiceDto>.Failure("Service not found.");

        var dto = new ServiceDto(
            service.Id,
            service.Name,
            service.Description,
            service.Price,
            service.DurationMinutes,
            service.IsActive,
            service.CreatedAt,
            service.UpdatedAt
        );

        return Result<ServiceDto>.Success(dto);
    }
}