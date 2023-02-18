package baovitt

sealed trait LasagnaError

case object FailedToParseError extends LasagnaError