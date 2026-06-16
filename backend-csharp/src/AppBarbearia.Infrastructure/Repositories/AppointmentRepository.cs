using Microsoft.EntityFrameworkCore;
using AppBarbearia.Domain.Entities;
using AppBarbearia.Domain.Enums;
using AppBarbearia.Domain.Interfaces;
using AppBarbearia.Infrastructure.Data;

namespace AppBarbearia.Infrastructure.Repositories;

public class AppointmentRepository(AppDbContext context)
    : Repository<Appointment>(context), IAppointmentRepository
{
    public async Task<Appointment?> GetByIdWithDetailsAsync(Guid id, CancellationToken cancellationToken = default)
        => await DbSet
            .Include(a => a.Client)
            .Include(a => a.Barber)
            .Include(a => a.Service)
            .FirstOrDefaultAsync(a => a.Id == id, cancellationToken);

    public async Task<IEnumerable<Appointment>> GetByClientAsync(Guid clientId, CancellationToken cancellationToken = default)
    => await DbSet
        .AsNoTracking()
        .Include(a => a.Client)   // ← ADICIONE ESTA LINHA
        .Include(a => a.Barber)
        .Include(a => a.Service)
        .Where(a => a.ClientId == clientId)
        .OrderByDescending(a => a.ScheduledAt)
        .ToListAsync(cancellationToken);

    public async Task<IEnumerable<Appointment>> GetByBarberAsync(Guid barberId, CancellationToken cancellationToken = default)
        => await DbSet
            .AsNoTracking()
            .Include(a => a.Client)
            .Include(a => a.Service)
            .Where(a => a.BarberId == barberId)
            .OrderBy(a => a.ScheduledAt)
            .ToListAsync(cancellationToken);

    public async Task<IEnumerable<Appointment>> GetByBarberAndDateAsync(Guid barberId, DateTime date, CancellationToken cancellationToken = default)
    {
        var startOfDay = date.Date;
        var endOfDay = startOfDay.AddDays(1);

        return await DbSet
            .AsNoTracking()
            .Include(a => a.Client)
            .Include(a => a.Service)
            .Where(a => a.BarberId == barberId
                     && a.ScheduledAt >= startOfDay
                     && a.ScheduledAt < endOfDay
                     && a.Status != AppointmentStatus.Cancelled)
            .OrderBy(a => a.ScheduledAt)
            .ToListAsync(cancellationToken);
    }

    /// <summary>
    /// Checks if the barber has any non-cancelled appointment
    /// that overlaps the proposed time slot [scheduledAt, scheduledAt + duration).
    /// </summary>
    public async Task<bool> HasConflictAsync(
        Guid barberId, DateTime scheduledAt, int durationMinutes,
        Guid? excludeId = null, CancellationToken cancellationToken = default)
    {
        var proposedEnd = scheduledAt.AddMinutes(durationMinutes);

        return await DbSet
            .AsNoTracking()
            .Include(a => a.Service)
            .Where(a => a.BarberId == barberId
                     && a.Status != AppointmentStatus.Cancelled
                     && (excludeId == null || a.Id != excludeId.Value))
            .AnyAsync(a =>
                scheduledAt < a.ScheduledAt.AddMinutes(a.Service.DurationMinutes)
             && proposedEnd > a.ScheduledAt,
            cancellationToken);
    }

    public async Task<IEnumerable<Appointment>> GetByBarberAndDateRangeAsync(
    Guid barberId, DateTime start, DateTime end, CancellationToken cancellationToken = default)
    => await DbSet
        .AsNoTracking()
        .Include(a => a.Service)
        .Where(a => a.BarberId == barberId
                 && a.ScheduledAt >= start
                 && a.ScheduledAt <= end)
        .OrderBy(a => a.ScheduledAt)
        .ToListAsync(cancellationToken);

    /// <summary>
    /// Busca todos os agendamentos FINALIZADOS (status = Finished) num intervalo de datas,
    /// incluindo o serviço — usado para relatório de faturamento.
    /// </summary>
    public async Task<IEnumerable<Appointment>> GetFinishedByDateRangeAsync(
        DateTime start, DateTime end, CancellationToken cancellationToken = default)
        => await DbSet
            .AsNoTracking()
            .Include(a => a.Service)
            .Where(a => a.Status == AppointmentStatus.Finished
                     && a.ScheduledAt >= start
                     && a.ScheduledAt <= end)
            .ToListAsync(cancellationToken);
}
