namespace AppBarbearia.Domain.Enums;

public enum AppointmentStatus
{
    Pending   = 1,  // Aguardando confirmação do barbeiro
    Confirmed = 2,  // Confirmado pelo barbeiro
    Finished  = 3,  // Finalizado
    Cancelled = 4   // Cancelado (cliente ou barbeiro)
}
