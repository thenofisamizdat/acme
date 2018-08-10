(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('QuestionnaireController', QuestionnaireController);

    QuestionnaireController.$inject = ['Questionnaire', 'QuestionnaireSearch'];

    function QuestionnaireController(Questionnaire, QuestionnaireSearch) {

        var vm = this;

        vm.questionnaires = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Questionnaire.query(function(result) {
                vm.questionnaires = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            QuestionnaireSearch.query({query: vm.searchQuery}, function(result) {
                vm.questionnaires = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
