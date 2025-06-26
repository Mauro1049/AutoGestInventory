AutoGest Inventory
Sistema de gestión de inventarios con autenticación de usuarios, permisos por módulos y soporte multiempresa. Desarrollado con Jetpack Compose y Supabase (PostgreSQL + Auth).

Características
API frontend (Jetpack Compose) con conexión directa a Supabase:

 Autenticación con Supabase (email + contraseña)

 Gestión de empresas

 Registro de usuarios con roles (superadmin, admin, usuario)

 Asignación de permisos por módulo

 Relación de usuarios por empresa

 Sin patrón MVVM: arquitectura sencilla, basada en clases auxiliares (crudUsuarios, crudModulos, etc.)

Requisitos
Android Studio (Hedgehog+)

SDK Android 34+

Supabase (proyecto ya creado)

Cuenta gratuita en https://supabase.com

Conexión a Internet

Instalación paso a paso
1. Clonar el repositorio
git clone https://github.com/tu_usuario/AutoGestInventory.git
cd AutoGestInventory

Configurar cliente Supabase
En SupabaseClient.kt, reemplaza con tus claves:
val supabase = createSupabaseClient(
    supabaseUrl = "https://<TU_SUPABASE_PROYECTO>.supabase.co",
    supabaseKey = "<CLAVE_PUBLICA>"
) {
    install(Auth)
    install(Postgrest)
}

Base de datos en Supabase
Tablas requeridas:
usuarios: contiene la información personal y tipo de usuario

empresa: datos de la empresa

modulos: lista de módulos del sistema

asignar_empresa: relación entre usuarios y empresas

permisos: módulos habilitados para cada usuario

Endpoints principales utilizados (Supabase)
Recurso	Tipo	Operación
usuarios	PostgREST + Auth	Insertar usuario con UID, consultar
modulos	PostgREST	Obtener módulos disponibles
permisos	PostgREST	Insertar múltiples permisos
asignar_empresa	PostgREST	Relación usuario-empresa

Seguridad
JWT de Supabase Auth para autenticar peticiones.

RLS activa en todas las tablas sensibles.

La sesión activa no se interrumpe al registrar nuevos usuarios (se usa auth.admin.createUser() si se requiere esto a futuro).

Scripts útiles
Ejecutar app en Android Studio (emulador o dispositivo físico).

Base de datos administrada vía panel de Supabase.

Arquitectura del Proyecto
Arquitectura simple sin MVVM.

Composables gestionan lógica directamente con clases como crudUsuarios.kt.

Componentes UI reutilizables (ej. ConfiguracionItem.kt).


