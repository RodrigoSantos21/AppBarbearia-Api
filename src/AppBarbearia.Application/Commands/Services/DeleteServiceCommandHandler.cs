using MediatR;
using AppBarbearia.Application.Common;
using AppBarbearia.Domain.Interfaces;

namespace AppBarbearia.Application.Commands.Services;

public sealed class DeleteServiceCommandHandler(IServiceRepository repository)
    : IRequestHandler<DeleteServiceCommand, Result>
{
    public async Task<Result> Handle(DeleteServiceCommand request, CancellationToken cancellationToken)
    {
        var service = await repository.GetByIdAsync(request.Id, cancellationToken);
        if (service is null)
            return Result.Failure($"Service '{request.Id}' not found.");

        service.Deactivate();
        repository.Update(service);
        await repository.SaveChangesAsync(cancellationToken);

        return Result.Success();
    }
}
