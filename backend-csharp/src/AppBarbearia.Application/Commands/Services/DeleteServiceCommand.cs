using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Commands.Services;

public record DeleteServiceCommand(Guid Id) : IRequest<Result>;
