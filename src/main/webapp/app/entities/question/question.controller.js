(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('QuestionController', QuestionController);

    QuestionController.$inject = ['Question', 'QuestionSearch'];

    function QuestionController(Question, QuestionSearch) {

        var vm = this;

        vm.questions = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Question.query(function(result) {
                vm.questions = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            QuestionSearch.query({query: vm.searchQuery}, function(result) {
                vm.questions = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
