using AppBarbearia.Domain.Entities;
using AppBarbearia.Domain.Enums;

namespace AppBarbearia.Domain.Interfaces;

public interface IAppointmentRepository : IRepository<Appointment>
{
    Task<Appointment?> GetByIdWithDetailsAsync(Guid id, CancellationToken cancellationToken = default);
    Task<IEnumerable<Appointment>> GetByClientAsync(Guid clientId, CancellationToken cancellationToken = default);
    Task<IEnumerable<Appointment>> GetByBarberAsync(Guid barberId, CancellationToken cancellationToken = default);
    Task<IEnumerable<Appointment>> GetByBarberAndDateAsync(Guid barberId, DateTime date, CancellationToken cancellationToken = default);
    Task<bool> HasConflictAsync(Guid barberId, DateTime scheduledAt, int durationMinutes, Guid? excludeId = null, CancellationToken cancellationToken = default);
}
