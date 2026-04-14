using AppBarbearia.Domain.Enums;

namespace AppBarbearia.Domain.Entities;

public class Appointment : BaseEntity
{
    public Guid ClientId   { get; private set; }
    public Guid BarberId   { get; private set; }
    public Guid ServiceId  { get; private set; }

    public DateTime ScheduledAt       { get; private set; }
    public AppointmentStatus Status   { get; private set; }
    public string? CancellationReason { get; private set; }

    public User    Client  { get; private set; } = null!;
    public User    Barber  { get; private set; } = null!;
    public Service Service { get; private set; } = null!;

    private Appointment() { }

    public static Appointment Create(Guid clientId, Guid barberId, Guid serviceId, DateTime scheduledAt)
        => new()
        {
            ClientId    = clientId,
            BarberId    = barberId,
            ServiceId   = serviceId,
            ScheduledAt = scheduledAt,
            Status      = AppointmentStatus.Pending
        };

    public void Confirm()
    {
        if (Status != AppointmentStatus.Pending)
            throw new InvalidOperationException("Only pending appointments can be confirmed.");
        Status = AppointmentStatus.Confirmed;
        SetUpdatedAt();
    }

    public void Finish()
    {
        if (Status != AppointmentStatus.Confirmed)
            throw new InvalidOperationException("Only confirmed appointments can be finished.");
        Status = AppointmentStatus.Finished;
        SetUpdatedAt();
    }

    public void Cancel(string reason)
    {
        if (Status == AppointmentStatus.Finished)
            throw new InvalidOperationException("Finished appointments cannot be cancelled.");
        Status             = AppointmentStatus.Cancelled;
        CancellationReason = reason;
        SetUpdatedAt();
    }

    public void Reschedule(DateTime newScheduledAt)
    {
        if (Status == AppointmentStatus.Finished || Status == AppointmentStatus.Cancelled)
            throw new InvalidOperationException("Cannot reschedule a finished or cancelled appointment.");
        ScheduledAt = newScheduledAt;
        Status      = AppointmentStatus.Pending;
        SetUpdatedAt();
    }
}
