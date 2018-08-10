(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('AnsweredQuestionnaireController', AnsweredQuestionnaireController);

    AnsweredQuestionnaireController.$inject = ['AnsweredQuestionnaire', 'AnsweredQuestionnaireSearch'];

    function AnsweredQuestionnaireController(AnsweredQuestionnaire, AnsweredQuestionnaireSearch) {

        var vm = this;

        vm.answeredQuestionnaires = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            AnsweredQuestionnaire.query(function(result) {
                vm.answeredQuestionnaires = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            AnsweredQuestionnaireSearch.query({query: vm.searchQuery}, function(result) {
                vm.answeredQuestionnaires = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
