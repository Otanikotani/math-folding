package org.github.otanikotani.mathfolding;


import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MathFoldingBuilder extends FoldingBuilderEx {

    private final MathFoldingEngine mathFoldingEngine = new MathFoldingEngine();

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        FoldingGroup group = FoldingGroup.newGroup("Math folding");

        List<FoldingDescriptor> descriptors = new ArrayList<>();
        Collection<PsiMethodCallExpression> methodCallExpressions =
                PsiTreeUtil.findChildrenOfType(root, PsiMethodCallExpression.class);

        List<PsiMethodCallExpression> parentless = methodCallExpressions.stream().filter(pmce -> {
            PsiElement hasMathParent = PsiTreeUtil.findFirstParent(pmce, true, psiElement -> {
                if (psiElement instanceof PsiMethodCallExpression) {
                    PsiMethodCallExpression parent = (PsiMethodCallExpression) psiElement;
                    return parent.getMethodExpression().getText().contains("Math.");
                }
                return false;
            });
            return hasMathParent == null;
        }).collect(Collectors.toList());

        parentless.forEach(expression -> {
            if (expression.getMethodExpression().getText().startsWith("Math.")) {
                descriptors.add(new FoldingDescriptor(expression.getNode(), expression.getTextRange(), group) {
                    @Nullable
                    @Override
                    public String getPlaceholderText() {
                        return mathFoldingEngine.fold(expression.getText());
                    }
                });
            }
        });
        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode astNode) {
        return "";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return true;
    }
}
