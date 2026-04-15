 using Microsoft.EntityFrameworkCore.Migrations;

using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;


#nullable disable


namespace SampleApp.Infrastructure.Migrations

{

    /// <inheritdoc />

    public partial class CreateAgendaSchema : Migration

    {

        /// <inheritdoc />

        protected override void Up(MigrationBuilder migrationBuilder)

        {

            // 1. Tabela de Usuários

            migrationBuilder.CreateTable(

                name: "usuarios",

                columns: table => new

                {

                    id_usuario = table.Column<int>(type: "integer", nullable: false)

                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),

                    nome = table.Column<string>(type: "character varying(150)", maxLength: 150, nullable: false),

                    email = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),

                    telefone = table.Column<string>(type: "character varying(20)", maxLength: 20, nullable: true),

                    tipo_conta = table.Column<int>(type: "integer", nullable: false),

                    ativo = table.Column<bool>(type: "boolean", nullable: true, defaultValue: true),

                    criado_em = table.Column<DateTime>(type: "timestamp with time zone", nullable: true, defaultValueSql: "CURRENT_TIMESTAMP"),

                    atualizado_em = table.Column<DateTime>(type: "timestamp with time zone", nullable: true, defaultValueSql: "CURRENT_TIMESTAMP")

                },

                constraints: table =>

                {

                    table.PrimaryKey("usuarios_pkey", x => x.id_usuario);

                    table.UniqueConstraint("usuarios_email_key", x => x.email);

                });


            // 2. Tabela de Serviços

            migrationBuilder.CreateTable(

                name: "servicos",

                columns: table => new

                {

                    id_servico = table.Column<int>(type: "integer", nullable: false)

                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),

                    nome = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),

                    preco = table.Column<decimal>(type: "numeric(10,2)", nullable: false),

                    duracao_media = table.Column<int>(type: "integer", nullable: false),

                    criado_em = table.Column<DateTime>(type: "timestamp", nullable: true, defaultValueSql: "CURRENT_TIMESTAMP"),

                    atualizado_em = table.Column<DateTime>(type: "timestamp", nullable: true, defaultValueSql: "CURRENT_TIMESTAMP")

                },

                constraints: table =>

                {

                    table.PrimaryKey("servicos_pkey", x => x.id_servico);

                });


            // 3. Tabela de Agenda

            migrationBuilder.CreateTable(

                name: "agenda",

                columns: table => new

                {

                    id_horario = table.Column<int>(type: "integer", nullable: false)

                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),

                    id_barbeiro = table.Column<int>(type: "integer", nullable: false),

                    id_cliente = table.Column<int>(type: "integer", nullable: false),

                    id_servico = table.Column<int>(type: "integer", nullable: false),

                    horario_marcado = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),

                    confirmado = table.Column<bool>(type: "boolean", nullable: true, defaultValue: false),

                    status = table.Column<string>(type: "character varying(50)", maxLength: 50, nullable: true),

                    observacoes = table.Column<string>(type: "text", nullable: true),

                    motivo_cancelamento = table.Column<string>(type: "text", nullable: true)

                },

                constraints: table =>

                {

                    table.PrimaryKey("agenda_pkey", x => x.id_horario);

                    table.ForeignKey(

                        name: "fk_barbeiro",

                        column: x => x.id_barbeiro,

                        principalTable: "usuarios",

                        principalColumn: "id_usuario");

                    table.ForeignKey(

                        name: "fk_cliente",

                        column: x => x.id_cliente,

                        principalTable: "usuarios",

                        principalColumn: "id_usuario");

                    table.ForeignKey(

                        name: "fk_servico",

                        column: x => x.id_servico,

                        principalTable: "servicos",

                        principalColumn: "id_servico");

                });


            // Índices para as chaves estrangeiras (boa prática para performance)

            migrationBuilder.CreateIndex(

                name: "IX_agenda_id_barbeiro",

                table: "agenda",

                column: "id_barbeiro");


            migrationBuilder.CreateIndex(

                name: "IX_agenda_id_cliente",

                table: "agenda",

                column: "id_cliente");


            migrationBuilder.CreateIndex(

                name: "IX_agenda_id_servico",

                table: "agenda",

                column: "id_servico");

        }


        /// <inheritdoc />

        protected override void Down(MigrationBuilder migrationBuilder)

        {

            migrationBuilder.DropTable(name: "agenda");

            migrationBuilder.DropTable(name: "servicos");

            migrationBuilder.DropTable(name: "usuarios");

        }

    }

}
