using MediatR;
using AppBarbearia.Application.Common;
using AppBarbearia.Domain.Entities;
using AppBarbearia.Domain.Enums;
using AppBarbearia.Domain.Interfaces;

namespace AppBarbearia.Application.Commands.Appointments;

public sealed class CreateAppointmentCommandHandler(
    IAppointmentRepository appointmentRepository,
    IServiceRepository     serviceRepository,
    IUserRepository        userRepository)
    : IRequestHandler<CreateAppointmentCommand, Result<Guid>>
{
    public async Task<Result<Guid>> Handle(CreateAppointmentCommand request, CancellationToken cancellationToken)
    {
        var client = await userRepository.GetByIdAsync(request.ClientId, cancellationToken);
        if (client is null || !client.IsActive)
            return Result<Guid>.Failure("Client not found or inactive.");

        var barber = await userRepository.GetByIdAsync(request.BarberId, cancellationToken);
        if (barber is null || !barber.IsActive)
            return Result<Guid>.Failure("Barber not found or inactive.");
        if (barber.Role != UserRole.Barber && barber.Role != UserRole.Admin)
            return Result<Guid>.Failure("The selected user is not a barber.");

        var service = await serviceRepository.GetByIdAsync(request.ServiceId, cancellationToken);
        if (service is null || !service.IsActive)
            return Result<Guid>.Failure("Service not found or inactive.");

        if (request.ScheduledAt <= DateTime.UtcNow)
            return Result<Guid>.Failure("Appointment must be scheduled in the future.");

        var hasConflict = await appointmentRepository.HasConflictAsync(
            request.BarberId, request.ScheduledAt, service.DurationMinutes, cancellationToken: cancellationToken);
        if (hasConflict)
            return Result<Guid>.Failure("The barber already has an appointment at this time.");

        var appointment = Appointment.Create(request.ClientId, request.BarberId, request.ServiceId, request.ScheduledAt);

        await appointmentRepository.AddAsync(appointment, cancellationToken);
        await appointmentRepository.SaveChangesAsync(cancellationToken);

        return Result<Guid>.Success(appointment.Id);
    }
}
