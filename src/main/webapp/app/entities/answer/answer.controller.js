(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('AnswerController', AnswerController);

    AnswerController.$inject = ['Answer', 'AnswerSearch'];

    function AnswerController(Answer, AnswerSearch) {

        var vm = this;

        vm.answers = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Answer.query(function(result) {
                vm.answers = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            AnswerSearch.query({query: vm.searchQuery}, function(result) {
                vm.answers = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
