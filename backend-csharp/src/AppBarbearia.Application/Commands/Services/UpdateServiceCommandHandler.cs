using MediatR;
using AppBarbearia.Application.Common;
using AppBarbearia.Domain.Interfaces;

namespace AppBarbearia.Application.Commands.Services;

public sealed class UpdateServiceCommandHandler(IServiceRepository repository)
    : IRequestHandler<UpdateServiceCommand, Result>
{
    public async Task<Result> Handle(UpdateServiceCommand request, CancellationToken cancellationToken)
    {
        var service = await repository.GetByIdAsync(request.Id, cancellationToken);
        if (service is null)
            return Result.Failure($"Service '{request.Id}' not found.");

        var nameConflict = await repository.ExistsByNameAsync(request.Name, request.Id, cancellationToken);
        if (nameConflict)
            return Result.Failure($"A service named '{request.Name}' already exists.");

        service.Update(request.Name, request.Description, request.Price, request.DurationMinutes);
        repository.Update(service);
        await repository.SaveChangesAsync(cancellationToken);

        return Result.Success();
    }
}
