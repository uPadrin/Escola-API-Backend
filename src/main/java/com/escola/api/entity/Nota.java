package com.escola.api.entity;

import com.escola.api.enums.Semestre;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notas",
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"aluno_id", "disciplina_id", "semestre", "ano"}
       ))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    private Usuario professor;

    @Column(name = "nota_bimestre1")
    private Double notaBimestre1;

    @Column(name = "nota_bimestre2")
    private Double notaBimestre2;

    @Column(name = "nota_recuperacao")
    private Double notaRecuperacao;

    @Column(name = "media_final")
    private Double mediaFinal;

    @Column(name = "faltas")
    private Integer faltas = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Semestre semestre;

    @Column(nullable = false)
    private Integer ano;

    @Column(name = "situacao", length = 20)
    private String situacao; // APROVADO, REPROVADO, CURSANDO, RECUPERACAO

    @Column(name = "lancado_em", nullable = false, updatable = false)
    private LocalDateTime lancadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void onCreate() {
        lancadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
        calcularMedia();
    }

    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
        calcularMedia();
    }

    public void calcularMedia() {
        if (notaBimestre1 != null && notaBimestre2 != null) {
            double media = (notaBimestre1 + notaBimestre2) / 2.0;

            if (media >= 7.0) {
                this.mediaFinal = media;
                this.situacao = "APROVADO";
            } else if (media >= 5.0 && notaRecuperacao != null) {
                // Media com recuperação
                double mediaComRec = (media + notaRecuperacao) / 2.0;
                this.mediaFinal = mediaComRec;
                this.situacao = mediaComRec >= 5.0 ? "APROVADO" : "REPROVADO";
            } else if (media >= 5.0) {
                this.mediaFinal = media;
                this.situacao = "RECUPERACAO";
            } else {
                this.mediaFinal = media;
                this.situacao = notaRecuperacao != null ? "REPROVADO" : "CURSANDO";
            }
        } else {
            this.situacao = "CURSANDO";
        }
    }
}
