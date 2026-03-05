#!/bin/bash

# SiteScanner API - Script de Execução
# Este script facilita a execução comum do projeto

echo "======================================"
echo "  SiteScanner API - Avenue Code Test"
echo "======================================"
echo ""

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Função para mostrar menu
show_menu() {
    echo "Escolha uma opção:"
    echo "1) Compilar o projeto (mvn clean install)"
    echo "2) Executar a aplicação (mvn spring-boot:run)"
    echo "3) Executar os testes (mvn test)"
    echo "4) Limpar e compilar (mvn clean package)"
    echo "5) Ver estrutura do projeto"
    echo "6) Sair"
    echo ""
}

# Função para verificar Java
check_java() {
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
        echo -e "${GREEN}✓ Java encontrado: $JAVA_VERSION${NC}"
        return 0
    else
        echo -e "${RED}✗ Java não encontrado. Por favor, instale o Java 17 ou superior.${NC}"
        return 1
    fi
}

# Função para verificar Maven
check_maven() {
    if command -v mvn &> /dev/null; then
        MAVEN_VERSION=$(mvn -version | head -n 1)
        echo -e "${GREEN}✓ Maven encontrado: $MAVEN_VERSION${NC}"
        return 0
    else
        echo -e "${RED}✗ Maven não encontrado. Por favor, instale o Maven.${NC}"
        return 1
    fi
}

# Verificar pré-requisitos
echo "Verificando pré-requisitos..."
check_java || exit 1
check_maven || exit 1
echo ""

# Loop do menu
while true; do
    show_menu
    read -p "Digite sua opção [1-6]: " choice
    echo ""
    
    case $choice in
        1)
            echo -e "${YELLOW}Compilando o projeto...${NC}"
            mvn clean install
            echo ""
            ;;
        2)
            echo -e "${YELLOW}Iniciando a aplicação...${NC}"
            echo -e "${GREEN}A aplicação estará disponível em: http://localhost:8080${NC}"
            echo -e "${GREEN}Console H2: http://localhost:8080/h2-console${NC}"
            echo ""
            mvn spring-boot:run
            ;;
        3)
            echo -e "${YELLOW}Executando os testes...${NC}"
            mvn test
            echo ""
            ;;
        4)
            echo -e "${YELLOW}Limpando e compilando...${NC}"
            mvn clean package
            echo ""
            ;;
        5)
            echo -e "${YELLOW}Estrutura do projeto:${NC}"
            tree -L 4 -I 'target' src/ 2>/dev/null || find src/ -type d | grep -v target
            echo ""
            ;;
        6)
            echo -e "${GREEN}Até logo!${NC}"
            exit 0
            ;;
        *)
            echo -e "${RED}Opção inválida. Por favor, escolha um número entre 1 e 6.${NC}"
            echo ""
            ;;
    esac
done
