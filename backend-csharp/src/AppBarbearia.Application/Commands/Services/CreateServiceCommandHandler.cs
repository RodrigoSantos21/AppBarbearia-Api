using MediatR;
using AppBarbearia.Application.Common;
using AppBarbearia.Domain.Entities;
using AppBarbearia.Domain.Interfaces;

namespace AppBarbearia.Application.Commands.Services;

public sealed class CreateServiceCommandHandler(IServiceRepository repository)
    : IRequestHandler<CreateServiceCommand, Result<Guid>>
{
    public async Task<Result<Guid>> Handle(CreateServiceCommand request, CancellationToken cancellationToken)
    {
        var exists = await repository.ExistsByNameAsync(request.Name, cancellationToken: cancellationToken);
        if (exists)
            return Result<Guid>.Failure($"A service named '{request.Name}' already exists.");

        var service = Service.Create(request.Name, request.Description, request.Price, request.DurationMinutes);

        await repository.AddAsync(service, cancellationToken);
        await repository.SaveChangesAsync(cancellationToken);

        return Result<Guid>.Success(service.Id);
    }
}
