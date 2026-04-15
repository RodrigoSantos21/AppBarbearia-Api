namespace AppBarbearia.Infrastructure.Settings;

public sealed class JwtSettings
{
    public const string SectionName = "JwtSettings";

    public string Secret           { get; init; } = string.Empty;
    public string Issuer           { get; init; } = string.Empty;
    public string Audience         { get; init; } = string.Empty;
    public int    ExpiresInSeconds { get; init; } = 3600;
}
