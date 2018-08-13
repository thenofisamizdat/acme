(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService', '$state', 'Questionnaire'];

    function HomeController ($scope, Principal, LoginService, $state, Questionnaire) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;

        vm.homePageState = "";

        vm.showQuestionnaire = false;
        vm.showQuestionnaireList = false;
        vm.showQuestionnaireAnswers = false;

        vm.questionnaire = {};

        vm.currentQuestionNumber = 0;
        vm.setQuestion = setQuestion;
        vm.question = {};

        vm.nextQuestion = nextQuestion;
        vm.completeQuestionnaire = completeQuestionnaire;

        vm.answers = [];
        vm.answer = "";

        vm.datePickerOpenStatus = {};

        vm.isSaving = false;

        vm.setHomeState = setHomeState;

        vm.openCalendar = openCalendar;
        vm.datePickerOpenStatus.answeredDate = false;

        function setHomeState(state, questionnaaire){
            vm.questionnaire = questionnaaire;
            vm.homePageState = state;

            vm.showQuestionnaire = false;
            vm.showQuestionnaireList = false;
            vm.showQuestionnaireAnswers = false;

            if (state == "questionnaire") {
                vm.showQuestionnaire = true;
                vm.currentQuestionNumber = 0;
                setQuestion();
            }
            if (state == "questionnaireList") vm.showQuestionnaireList = true;
            if (state == "questionnaireAnswers") vm.showQuestionnaireAnswers = true;
        }

        function nextQuestion(){
            console.log("next")
            vm.questionnaire.ids[vm.currentQuestionNumber].answers = vm.answer; // add answer to question
            vm.currentQuestionNumber++;
            vm.answers.push(vm.answer);
            vm.answer = "";
            setQuestion();
        }
        function setQuestion(){
            vm.question = vm.questionnaire.ids[vm.currentQuestionNumber];
        }
        function completeQuestionnaire(){
            save();

        }

        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        loadAll();

        function loadAll() {
            Questionnaire.query(function(result) {
                vm.questionnaires = result;
                vm.searchQuery = null;

                if (!vm.checkAdmin()) vm.showQuestionnaireList = true;
            });
        }

        function save () {
            vm.isSaving = true;
            if (vm.questionnaire.id !== null) {
                Questionnaire.update(vm.questionnaire, onSaveSuccess, onSaveError);
            } else {
                Questionnaire.save(vm.questionnaire, onSaveSuccess, onSaveError);
            }
        }
        function onSaveSuccess (result) {
            $scope.$emit('acmeApp:questionnaireUpdate', result);
            // $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.checkAdmin = checkAdmin;

        function checkAdmin(){
            return vm.account.login == "admin";
        }

        getAccount();

        function openCalendar (date) {
            console.log("hhhh")
            vm.datePickerOpenStatus[date] = true;
        }

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }
        function register () {
            $state.go('register');
        }
    }
})();
