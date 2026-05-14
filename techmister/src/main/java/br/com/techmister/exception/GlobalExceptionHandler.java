package br.com.techmister.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ApiErrorResponse> tratarRegraNegocio(RegraDeNegocioException ex, HttpServletRequest req) {
        return montar(HttpStatus.BAD_REQUEST, "REGRA_NEGOCIO", ex.getMessage(), req, null);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> tratarNaoEncontrado(RecursoNaoEncontradoException ex, HttpServletRequest req) {
        return montar(HttpStatus.NOT_FOUND, "NAO_ENCONTRADO", ex.getMessage(), req, null);
    }

    /** Erros de validação do Bean Validation (@Valid). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> tratarValidacao(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ApiErrorResponse.CampoInvalido> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toCampoInvalido)
                .toList();
        return montar(HttpStatus.BAD_REQUEST, "VALIDACAO", "Um ou mais campos estão inválidos.", req, campos);
    }

    @ExceptionHandler({ BadCredentialsException.class, AuthenticationException.class })
    public ResponseEntity<ApiErrorResponse> tratarAutenticacao(Exception ex, HttpServletRequest req) {
        return montar(HttpStatus.UNAUTHORIZED, "NAO_AUTENTICADO", "Usuário ou senha inválidos", req, null);
    }

    /** Fallback: qualquer exceção não prevista vira 500 com mensagem genérica. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> tratarFallback(Exception ex, HttpServletRequest req) {
        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "ERRO_INTERNO",
                "Ocorreu um erro inesperado. Tente novamente em instantes.", req, null);
    }

    // ---------- helpers ----------

    private ApiErrorResponse.CampoInvalido toCampoInvalido(FieldError fe) {
        return new ApiErrorResponse.CampoInvalido(fe.getField(), fe.getDefaultMessage());
    }

    private ResponseEntity<ApiErrorResponse> montar(HttpStatus status, String codigo, String msg,
                                                    HttpServletRequest req,
                                                    List<ApiErrorResponse.CampoInvalido> campos) {
        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .erro(codigo)
                .mensagem(msg)
                .path(req.getRequestURI())
                .camposInvalidos(campos)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
