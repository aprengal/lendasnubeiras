# Lendas Nubeiras

Aplicación móbil para o fomento de actividades interxeracionais.

**Lendas Nubeiras** é unha aplicación que permite descubrir, organizar e participar en actividades pensadas para distintos grupos de idade, coa finalidade de favorecer o encontro e a interacción entre persoas de diferentes xeracións.

## 🎯 Obxectivos

- Desenvolver unha aplicación móbil nativa para Android que permita a xestión e participación en actividades dirixidas a distintos grupos de idade.
- Favorecer o encontro e a interacción entre persoas de diferentes xeracións a través de actividades compartidas.
- Deseñar unha arquitectura cliente-servidor escalable, empregando unha API propia e unha base de datos relacional.
- Aplicar unha interface de usuario moderna, accesible e intuitiva, adaptada tanto a persoas maiores como a usuarios máis novos.
- Xestionar todo o ciclo de vida do proxecto empregando ferramentas de control de versións e metodoloxías propias do desenvolvemento de software.

## 🛠️ Tecnoloxías

| Compoñente            | Tecnoloxía                   |
|-----------------------|------------------------------|
| App móbil             | Kotlin + Jetpack Compose     |
| API / Backend         | PHP                          |
| Base de datos         | MySQL                        |
| Aloxamento (temporal) | VPS en Google Cloud Platform |

## 📂 Estrutura do repositorio

```
lendasnubeiras/           (privado)
├── app/                  # Módulo que ensambla e permite arrincar a aplicación
├── data/                 # Datos e lóxica de acceso a datos
├── docs/                 # Documentación técnica compilada (servida por GitHub Pages)
├── ui/                   # Interface de usuario (Jetpack Compose)
├── servidorapi/          # Código fonte da API REST
└── gradle/               # Configuración de Gradle
```

## 📖 Documentación

A documentación completa do proxecto atópase en pdf no directorio de documentos: https://github.com/aprengal/lendasnubeiras/tree/main/docs

## 🚀 Estado do proxecto

🔧 En desenvolvemento — Proxecto Final de Ciclo (2026).

Falta crear a API e o sistema que permite cargar información das actividades sen ter que cargalas en memoria como os textos da interface.

## ✍️ Autor

*Diego Villar*

## 📄 Licenza

Este proxecto está baixo a licenza MIT, aínda que hai varias fontes que contan con outra licenza
